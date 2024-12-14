package com.pixelfabric.minigames;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.*;
import java.util.function.Consumer;

public class PinataMinigame {
    private static PinataMinigame instance;
    private boolean testMode = false;
    private int minPlayers = 2;
    private int maxPlayers = 8;

    public static PinataMinigame getInstance() {
        if (instance == null) {
            throw new IllegalStateException("PinataMinigame no ha sido inicializado");
        }
        return instance;
    }

    public static void initialize(BlockPos arenaSpawn, BlockPos lobbySpawn) {
        instance = new PinataMinigame(arenaSpawn, lobbySpawn);
    }

    public void setMinPlayers(int count) {
        this.minPlayers = count;
    }

    public void setMaxPlayers(int count) {
        this.maxPlayers = count;
    }

    private enum GameState {
        WAITING,
        STARTING,
        IN_PROGRESS,
        ENDING
    }

    public void startTestMode() {
        this.testMode = true;
        this.minPlayers = 1;
        currentState = GameState.IN_PROGRESS;
        roundNumber = 1;

        if (playerQueue.isEmpty()) {
            broadcastMessage("§eModo prueba activado. Use /pinata join para unirse.");
            currentState = GameState.WAITING;
            return;
        }

        nextPlayer();
        broadcastMessage("§a¡Modo prueba iniciado!");
    }

    private static class PlayerScore {
        private final UUID playerId;
        private int points;
        private List<String> rewards;

        public PlayerScore(UUID playerId) {
            this.playerId = playerId;
            this.points = 0;
            this.rewards = new ArrayList<>();
        }

        public void resetPoints() {
            this.points = 0;
        }
    }

    private GameState currentState;
    private final Queue<ServerPlayerEntity> playerQueue;
    private final Map<UUID, PlayerScore> playerScores;
    private ServerPlayerEntity currentPlayer;
    private final BlockPos arenaSpawnPoint;
    private final BlockPos lobbySpawnPoint;
    private int roundNumber;
    private static final int MIN_PLAYERS = 2;
    private static final int MAX_PLAYERS = 8;
    private static final int COUNTDOWN_SECONDS = 10;
    private int timeRemaining;

    // Recompensas y castigos
    private static final List<RewardAction> POSITIVE_REWARDS = Arrays.asList(
            new RewardAction("¡Diamantes!", player -> giveItem(player, Items.DIAMOND, 5), 100),
            new RewardAction("¡Netherite!", player -> giveItem(player, Items.NETHERITE_INGOT, 1), 150),
            new RewardAction("¡Super Velocidad!", player ->
                    player.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, 600, 1)), 75),
            new RewardAction("¡Poder de Salto!", player ->
                    player.addStatusEffect(new StatusEffectInstance(StatusEffects.JUMP_BOOST, 600, 2)), 80),
            new RewardAction("¡Regeneración!", player ->
                    player.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 200, 1)), 90)
    );

    private static final List<RewardAction> NEGATIVE_REWARDS = Arrays.asList(
            new RewardAction("¡Ceguera Temporal!", player ->
                    player.addStatusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS, 200, 0)), -30),
            new RewardAction("¡Levitación Incontrolable!", player ->
                    player.addStatusEffect(new StatusEffectInstance(StatusEffects.LEVITATION, 100, 1)), -40),
            new RewardAction("¡Confusión Total!", player -> {
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.NAUSEA, 200, 0));
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 200, 1));
            }, -45),
            new RewardAction("¡Lluvia Personal!", player -> {
                // Implementar efecto de partículas de lluvia alrededor del jugador
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.UNLUCK, 600, 0));
            }, -35)
    );

    private static void giveItem(PlayerEntity player, Item item, int amount) {
        ItemStack itemStack = new ItemStack(item, amount);
        boolean added = player.getInventory().insertStack(itemStack);

        if (!added) {
            // Si el inventario está lleno, dropear el item a los pies del jugador
            player.dropItem(itemStack, true);
        }
    }

    public PinataMinigame(BlockPos arenaSpawn, BlockPos lobbySpawn) {
        this.currentState = GameState.WAITING;
        this.playerQueue = new LinkedList<>();
        this.playerScores = new HashMap<>();
        this.arenaSpawnPoint = arenaSpawn;
        this.lobbySpawnPoint = lobbySpawn;
        this.roundNumber = 0;
        this.timeRemaining = COUNTDOWN_SECONDS;
    }

    public void addPlayer(ServerPlayerEntity player) {
        if (currentState == GameState.WAITING && playerQueue.size() < maxPlayers) {
            playerQueue.offer(player);
            playerScores.put(player.getUuid(), new PlayerScore(player.getUuid()));
            player.teleport(lobbySpawnPoint.getX(), lobbySpawnPoint.getY(), lobbySpawnPoint.getZ());
            broadcastMessage("§a¡" + player.getName().getString() + " se ha unido! §7(" +
                    playerQueue.size() + "/" + maxPlayers + " jugadores)");

            if (testMode || playerQueue.size() >= minPlayers) {
                startCountdown();
            }
        }
    }

    public String getGameStatus() {
        StringBuilder status = new StringBuilder();
        status.append("§6Estado: §f").append(currentState.toString()).append("\n");
        status.append("§6Modo prueba: §f").append(testMode ? "Activado" : "Desactivado").append("\n");
        status.append("§6Jugadores: §f").append(playerQueue.size()).append("/").append(maxPlayers).append("\n");
        status.append("§6Mínimo requerido: §f").append(minPlayers).append("\n");

        if (currentPlayer != null) {
            status.append("§6Jugador actual: §f").append(currentPlayer.getName().getString()).append("\n");
        }

        if (!playerQueue.isEmpty()) {
            status.append("§6Jugadores en cola: §f");
            playerQueue.forEach(p -> status.append(p.getName().getString()).append(", "));
        }

        return status.toString();
    }

    private void startCountdown() {
        currentState = GameState.STARTING;
        broadcastMessage("¡El juego comenzará en " + COUNTDOWN_SECONDS + " segundos!");
        // Implementar contador usando tasks
    }

    public void startGame() {
        if (currentState != GameState.WAITING) {
            broadcastMessage("El juego ya está en progreso.");
            return;
        }

        if (playerQueue.size() < MIN_PLAYERS) {
            broadcastMessage("No hay suficientes jugadores para comenzar (mínimo " + MIN_PLAYERS + ")");
            return;
        }

        currentState = GameState.IN_PROGRESS;
        roundNumber = 1;
        nextPlayer();
        broadcastMessage("¡Comienza el juego! Ronda " + roundNumber);
    }

    private void nextPlayer() {
        if (currentPlayer != null) {
            // Resetear puntos del jugador actual antes de cambiar
            PlayerScore currentScore = playerScores.get(currentPlayer.getUuid());
            if (currentScore != null) {
                currentScore.resetPoints();
            }
            currentPlayer.teleport(lobbySpawnPoint.getX(), lobbySpawnPoint.getY(), lobbySpawnPoint.getZ());
        }

        currentPlayer = playerQueue.poll();
        if (currentPlayer == null) {
            endGame("¡Todos los jugadores han terminado sus turnos!");
            return;
        }

        playerQueue.offer(currentPlayer); // Volver a añadir al final de la cola
        currentPlayer.teleport(arenaSpawnPoint.getX(), arenaSpawnPoint.getY(), arenaSpawnPoint.getZ());
        broadcastMessage("¡Turno de " + currentPlayer.getName().getString() + "!");
    }

    public void processHit(ServerPlayerEntity player) {
        if (currentState != GameState.IN_PROGRESS || !player.equals(currentPlayer)) {
            return;
        }

        PlayerScore score = playerScores.get(player.getUuid());
        if (score == null) return;

        // Procesar el tercer golpe
        if (score.points == 2) {
            processThirdHit(player);
            nextPlayer();
        } else {
            score.points++;
            broadcastMessage("¡Golpe " + score.points + "/3!");
        }
    }

    private void processThirdHit(ServerPlayerEntity player) {
        // 50% probabilidad de recompensa positiva o negativa
        if (Math.random() < 0.5) {
            RewardAction reward = getRandomReward(POSITIVE_REWARDS);
            reward.apply(player);
            playerScores.get(player.getUuid()).rewards.add(reward.getName());
            broadcastMessage("¡" + player.getName().getString() + " obtuvo: " + reward.getName() + "!");
        } else {
            RewardAction punishment = getRandomReward(NEGATIVE_REWARDS);
            punishment.apply(player);
            playerScores.get(player.getUuid()).rewards.add(punishment.getName());
            broadcastMessage("¡Oh no! " + player.getName().getString() + " recibió: " + punishment.getName() + "!");
        }
    }

    public void endGame(String reason) {
        currentState = GameState.ENDING;
        broadcastMessage("¡Fin del juego! " + reason);

        // Mostrar resumen
        StringBuilder summary = new StringBuilder("Resumen del juego:\n");
        playerScores.forEach((uuid, score) -> {
            ServerPlayerEntity player = getPlayerByUUID(uuid);
            if (player != null) {
                summary.append(player.getName().getString())
                        .append(": ")
                        .append(String.join(", ", score.rewards))
                        .append("\n");
            }
        });

        broadcastMessage(summary.toString());

        // Limpiar estado
        playerQueue.clear();
        playerScores.clear();
        currentPlayer = null;
        currentState = GameState.WAITING;
    }

    private static RewardAction getRandomReward(List<RewardAction> rewards) {
        return rewards.get(new Random().nextInt(rewards.size()));
    }

    private void broadcastMessage(String message) {
        playerQueue.forEach(player ->
                player.sendMessage(Text.literal(message)));
    }

    // Métodos auxiliares
    private static void giveReward(PlayerEntity player, String item, int amount) {
        // Implementar lógica para dar items
    }

    private static void spawnHostileMob(PlayerEntity player, String mobType) {
        // Implementar lógica para spawneo de mobs
    }

    private static void spawnMultipleHostileMobs(PlayerEntity player, String mobType, int count) {
        // Implementar lógica para spawneo múltiple
    }

    private ServerPlayerEntity getPlayerByUUID(UUID uuid) {
        return playerQueue.stream()
                .filter(p -> p.getUuid().equals(uuid))
                .findFirst()
                .orElse(null);
    }
}

class RewardAction {
    private final String name;
    private final Consumer<ServerPlayerEntity> action;
    private final int pointValue;

    public RewardAction(String name, Consumer<ServerPlayerEntity> action, int pointValue) {
        this.name = name;
        this.action = action;
        this.pointValue = pointValue;
    }

    public String getName() { return name; }
    public void apply(ServerPlayerEntity player) { action.accept(player); }
    public int getPointValue() { return pointValue; }
}