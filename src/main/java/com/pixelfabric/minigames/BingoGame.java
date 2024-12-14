package com.pixelfabric.minigames;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.Stat;
import net.minecraft.text.Text;
import net.minecraft.network.packet.s2c.play.TitleS2CPacket;
import net.minecraft.network.packet.s2c.play.SubtitleS2CPacket;
import net.minecraft.scoreboard.*;
import net.minecraft.item.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.command.CommandManager;
import net.minecraft.sound.SoundEvents;
import net.minecraft.sound.SoundCategory;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.stat.Stats;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.util.Identifier;

import java.util.*;

public class BingoGame {
    private static final int GRID_SIZE = 4;
    private static final int OBJECTIVES_TO_SHOW = 13;
    private static final int TOTAL_OBJECTIVES = GRID_SIZE * GRID_SIZE;
    private static final int[] ROUND_DURATIONS = {6000, 8000, 10000};

    private final Map<ServerPlayerEntity, boolean[]> playerProgress;
    private final Map<ServerPlayerEntity, Integer> playerScores;
    private final List<List<BingoObjective>> roundObjectives;
    private final List<List<BingoObjective>> activeObjectives;
    private boolean isGameActive;
    private int timeRemaining;
    private int currentRound;
    private ServerPlayerEntity currentWinner;

    private static BingoGame instance;


    public static BingoGame getInstance() {
        if (instance == null) {
            instance = new BingoGame();
        }
        return instance;
    }

    private BingoGame() {
        this.playerProgress = new HashMap<>();
        this.playerScores = new HashMap<>();
        this.roundObjectives = new ArrayList<>();
        this.activeObjectives = new ArrayList<>();
        this.isGameActive = false;
        this.currentRound = 0;
        initializeAllRoundObjectives();
    }
    private void selectRandomObjectives() {
        activeObjectives.clear();
        Random random = new Random();

        for (List<BingoObjective> roundList : roundObjectives) {
            List<BingoObjective> available = new ArrayList<>(roundList);
            List<BingoObjective> selected = new ArrayList<>();

            for (int i = 0; i < OBJECTIVES_TO_SHOW; i++) {
                int index = random.nextInt(available.size());
                selected.add(available.get(index));
                available.remove(index);
            }

            activeObjectives.add(selected);
        }
    }

    private void initializeAllRoundObjectives() {
        // Ronda 1
        List<BingoObjective> round1 = new ArrayList<>();
        round1.add(new BingoObjective("⚒ Pico", Items.WOODEN_PICKAXE, "Craftea un pico de madera", 1));
        round1.add(new BingoObjective("📦 Cofre", Items.CHEST, "Consigue un cofre", 1));
        round1.add(new BingoObjective("🚪 Puerta", Items.OAK_DOOR, "Craftea una puerta", 1));
        round1.add(new BingoObjective("🍞 Pan", Items.BREAD, "Hornea pan", 1));
        round1.add(new BingoObjective("🔥 Horno", Items.FURNACE, "Construye un horno", 1));
        round1.add(new BingoObjective("🕯 Antorcha", Items.TORCH, "Crea antorchas", 1));
        round1.add(new BingoObjective("📋 Mesa", Items.CRAFTING_TABLE, "Craftea mesa de trabajo", 1));
        round1.add(new BingoObjective("🧶 Lana", Items.WHITE_WOOL, "Obtén lana", 1));
        round1.add(new BingoObjective("⚔ Espada", Items.WOODEN_SWORD, "Craftea espada de madera", 1));
        round1.add(new BingoObjective("🪣 Balde", Items.BUCKET, "Crea un balde", 1));
        round1.add(new BingoObjective("🪜 Escalera", Items.LADDER, "Construye escaleras", 1));
        round1.add(new BingoObjective("🏹 Arco", Items.BOW, "Fabrica un arco", 1));
        round1.add(new BingoObjective("➶ Flecha", Items.ARROW, "Crea flechas", 1));
        round1.add(new BingoObjective("⛏ Pala", Items.WOODEN_SHOVEL, "Craftea una pala", 1));
        round1.add(new BingoObjective("🪓 Hacha", Items.WOODEN_AXE, "Craftea un hacha", 1));
        round1.add(new BingoObjective("🛏 Cama", Items.RED_BED, "Construye una cama", 1));

        // Ronda 2
        List<BingoObjective> round2 = new ArrayList<>();
        round2.add(new BingoObjective("⚔ Espada Hierro", Items.IRON_SWORD, "Craftea espada de hierro", 2));
        round2.add(new BingoObjective("🎣 Caña", Items.FISHING_ROD, "Craftea caña de pescar", 2));
        round2.add(new BingoObjective("🛡 Escudo", Items.SHIELD, "Craftea un escudo", 2));
        round2.add(new BingoObjective("👕 Armadura", Items.IRON_CHESTPLATE, "Craftea pechera de hierro", 2));
        round2.add(new BingoObjective("🥾 Botas", Items.IRON_BOOTS, "Craftea botas de hierro", 2));
        round2.add(new BingoObjective("🪨 Piedra Lisa", Items.SMOOTH_STONE, "Hornea piedra lisa", 2));
        round2.add(new BingoObjective("🧪 Pociones", Items.BREWING_STAND, "Craftea soporte pociones", 2));
        round2.add(new BingoObjective("📝 Libro", Items.BOOK, "Craftea un libro", 2));
        round2.add(new BingoObjective("🎨 Tintes", Items.RED_DYE, "Obtén tinte rojo", 2));
        round2.add(new BingoObjective("🪜 Andamio", Items.SCAFFOLDING, "Craftea andamio", 2));
        round2.add(new BingoObjective("🎭 Calabaza", Items.CARVED_PUMPKIN, "Talla una calabaza", 2));
        round2.add(new BingoObjective("🏹 Ballesta", Items.CROSSBOW, "Craftea una ballesta", 2));
        round2.add(new BingoObjective("🎯 Diana", Items.TARGET, "Craftea una diana", 2));
        round2.add(new BingoObjective("🚂 Vagoneta", Items.MINECART, "Craftea una vagoneta", 2));
        round2.add(new BingoObjective("⛵ Bote", Items.OAK_BOAT, "Craftea un bote", 2));
        round2.add(new BingoObjective("🏃 Sprint", null, "Corre 500 bloques", 2));

        // Ronda 3
        List<BingoObjective> round3 = new ArrayList<>();
        round3.add(new BingoObjective("💎 Diamante", Items.DIAMOND, "Encuentra diamante", 3));
        round3.add(new BingoObjective("⚔ Espada Diamante", Items.DIAMOND_SWORD, "Craftea espada diamante", 3));
        round3.add(new BingoObjective("🗺 Mapa", Items.MAP, "Craftea un mapa", 3));
        round3.add(new BingoObjective("🧭 Brújula", Items.COMPASS, "Craftea una brújula", 3));
        round3.add(new BingoObjective("⚡ TNT", Items.TNT, "Craftea TNT", 3));
        round3.add(new BingoObjective("📻 Jukebox", Items.JUKEBOX, "Craftea jukebox", 3));
        round3.add(new BingoObjective("🎵 Disco", Items.MUSIC_DISC_13, "Encuentra un disco", 3));
        round3.add(new BingoObjective("🏃 Maratón", null, "Corre 1000 bloques", 3));
        round3.add(new BingoObjective("☠ Superviviente", null, "Sobrevive 10 min", 3));
        round3.add(new BingoObjective("🐮 Ganadero", null, "Cría 3 animales", 3));
        round3.add(new BingoObjective("⛏ Minero", null, "Mina 64 piedra", 3));
        round3.add(new BingoObjective("🌳 Leñador", null, "Tala 32 árboles", 3));
        round3.add(new BingoObjective("🎯 Arquero", null, "Mata 5 mobs", 3));
        round3.add(new BingoObjective("💀 Cazador", null, "Mata un esqueleto", 3));
        round3.add(new BingoObjective("🌊 Pescador", null, "Pesca 3 peces", 3));
        round3.add(new BingoObjective("🏆 Explorador", null, "Visita 3 biomas", 3));

        roundObjectives.add(round1);
        roundObjectives.add(round2);
        roundObjectives.add(round3);
    }

    public static void registerCommands(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("pxbingo")
                .executes(context -> {
                    getInstance().startGame(Collections.singletonList(context.getSource().getPlayer()));
                    return 1;
                }));

        dispatcher.register(CommandManager.literal("pxfbingo")
                .executes(context -> {
                    getInstance().endGame();
                    return 1;
                }));

        dispatcher.register(CommandManager.literal("pxwbingo")
                .executes(context -> {
                    getInstance().showAllScores();
                    return 1;
                }));
    }

    private void showAllScores() {
        if (!isGameActive && playerScores.isEmpty()) {
            broadcastMessage("§cNo hay puntuaciones disponibles.");
            return;
        }

        List<Map.Entry<ServerPlayerEntity, Integer>> sortedScores = new ArrayList<>(playerScores.entrySet());
        sortedScores.sort((a, b) -> b.getValue().compareTo(a.getValue()));

        broadcastMessage("§6=== §lPuntuaciones del Bingo §r§6===");
        for (Map.Entry<ServerPlayerEntity, Integer> entry : sortedScores) {
            String playerName = entry.getKey().getName().getString();
            int score = entry.getValue();
            broadcastMessage(String.format("§e%s: §f%d puntos", playerName, score));
        }

        if (!sortedScores.isEmpty()) {
            ServerPlayerEntity winner = sortedScores.get(0).getKey();
            int winningScore = sortedScores.get(0).getValue();
            announceWinner(winner, winningScore);
        }
    }

    private void broadcastMessage(String message) {
        for (ServerPlayerEntity player : playerProgress.keySet()) {
            player.sendMessage(Text.literal(message));
        }
    }

    private Text createObjectiveDisplay(BingoObjective objective, boolean completed) {
        String completionMark = completed ? "§a✔" : "§7◯";
        return Text.literal(String.format("%s %s §f%s",
                completionMark,
                objective.getEmoji(),
                objective.getName()));
    }

    private void setupScoreboard(ServerPlayerEntity player) {
        Scoreboard scoreboard = player.getServer().getScoreboard();
        String objectiveName = "bingo" + currentRound;
        ScoreboardObjective objective = scoreboard.getObjective(objectiveName);

        if (objective != null) {
            scoreboard.removeObjective(objective);
        }

        objective = scoreboard.addObjective(
                objectiveName,
                ScoreboardCriterion.DUMMY,
                Text.literal(String.format("§6§l⚔ BINGO - Ronda %d ⚔", currentRound + 1)),
                ScoreboardCriterion.RenderType.INTEGER
        );

        scoreboard.setObjectiveSlot(1, objective);

        addScoreboardRow(scoreboard, objective, "§e§l==================", "", 20);
        addScoreboardRow(scoreboard, objective,
                String.format("§e⌚ Tiempo: §f%s", formatTime(timeRemaining)), "", 19);
        addScoreboardRow(scoreboard, objective,
                String.format("§fPuntos: §a%d", playerScores.get(player)), "", 18);
        addScoreboardRow(scoreboard, objective, "§e§l==================", "", 17);

        List<BingoObjective> currentObjectives = activeObjectives.get(currentRound);
        boolean[] progress = playerProgress.get(player);
        for (int i = 0; i < currentObjectives.size(); i++) {
            Text displayText = createObjectiveDisplay(currentObjectives.get(i), progress[i]);
            addScoreboardRow(scoreboard, objective, displayText.getString(), "", 16 - i);
        }

        addScoreboardRow(scoreboard, objective, "§e§l==================", "", 0);
        int completed = countCompletedObjectives(progress);
        addScoreboardRow(scoreboard, objective,
                String.format("§fCompletados: §a%d§7/§c%d", completed, OBJECTIVES_TO_SHOW), "", -1);
    }

    private void addScoreboardRow(Scoreboard scoreboard, ScoreboardObjective objective, String text, String suffix, int score) {
        Team team = scoreboard.getTeam(text);
        if (team == null) {
            team = scoreboard.addTeam(text);
            team.setPrefix(Text.literal(text));
            team.setSuffix(Text.literal(suffix));
        }
        scoreboard.getPlayerScore(text, objective).setScore(score);
    }

    private String formatTime(int ticks) {
        int seconds = ticks / 20;
        int minutes = seconds / 60;
        seconds = seconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    public void startGame(List<ServerPlayerEntity> players) {
        if (isGameActive) {
            players.forEach(p -> p.sendMessage(Text.literal("§c¡El juego ya está en curso!")));
            return;
        }

        isGameActive = true;
        currentRound = 0;
        timeRemaining = ROUND_DURATIONS[currentRound];

        selectRandomObjectives();

        for (ServerPlayerEntity player : players) {
            playerProgress.put(player, new boolean[OBJECTIVES_TO_SHOW]);
            playerScores.put(player, 0);
            setupScoreboard(player);

            player.playSound(SoundEvents.ENTITY_PLAYER_LEVELUP, SoundCategory.PLAYERS, 1.0f, 1.0f);
            player.networkHandler.sendPacket(new TitleS2CPacket(Text.literal("§6§l¡BINGO!")));
            player.networkHandler.sendPacket(new SubtitleS2CPacket(Text.literal("§e¡Ronda " + (currentRound + 1) + " iniciada!")));

            player.sendMessage(Text.literal("§a¡El juego de Bingo ha comenzado!"));
            player.sendMessage(Text.literal("§e➤ Completa los objetivos para ganar puntos"));
            player.sendMessage(Text.literal("§e➤ Cada objetivo completado vale " + (currentRound + 1) + " puntos"));
            player.sendMessage(Text.literal("§e➤ ¡Tienes " + (ROUND_DURATIONS[currentRound] / 1200) + " minutos para esta ronda!"));
        }
    }


    public void checkPlayerActivity(ServerPlayerEntity player) {
        if (!isGameActive) return;

        if (!playerProgress.containsKey(player)) {
            playerProgress.put(player, new boolean[OBJECTIVES_TO_SHOW]);
        }
        if (!playerScores.containsKey(player)) {
            playerScores.put(player, 0);
        }

        List<BingoObjective> currentObjectives = activeObjectives.get(currentRound);
        boolean[] progress = playerProgress.get(player);

        for (int i = 0; i < currentObjectives.size(); i++) {
            BingoObjective objective = currentObjectives.get(i);

            if (!progress[i] && checkObjectiveCompletion(player, objective)) {
                completeObjective(player, i, objective);
            }
        }
    }

    private void completeObjective(ServerPlayerEntity player, int objectiveIndex, BingoObjective objective) {
        boolean[] progress = playerProgress.get(player);
        if (progress[objectiveIndex]) return;

        progress[objectiveIndex] = true;

        int pointsEarned = currentRound + 1;
        playerScores.put(player, playerScores.get(player) + pointsEarned);

        updateScoreboard(player, objectiveIndex);

        player.sendMessage(Text.literal("§a¡Objetivo completado: " + objective.getEmoji() + " " + objective.getName() + "! §e(+" + pointsEarned + " puntos)"));
        player.playSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.PLAYERS, 1.0f, 1.0f);

        if (checkRoundCompletion(progress)) {
            if (currentRound < ROUND_DURATIONS.length - 1) {
                startNextRound(player);
            } else {
                currentWinner = player;
                announceWinner(player, playerScores.get(player));
                endGame();
            }
        }
    }

    private boolean checkObjectiveCompletion(ServerPlayerEntity player, BingoObjective objective) {
        if (objective.getRequiredItem() != null) {
            ItemStack heldItem = player.getMainHandStack();
            return !heldItem.isEmpty() && heldItem.getItem() == objective.getRequiredItem();
        }

        String objectiveName = objective.getName().toLowerCase();

        if (objectiveName.contains("maratón")) {
            Stat<Identifier> walkStat = Stats.CUSTOM.getOrCreateStat(Stats.WALK_ONE_CM);
            return player.getStatHandler().getStat(walkStat) >= 100000;
        }
        if (objectiveName.contains("sprint")) {
            Stat<Identifier> sprintStat = Stats.CUSTOM.getOrCreateStat(Stats.SPRINT_ONE_CM);
            return player.getStatHandler().getStat(sprintStat) >= 50000;
        }
        if (objectiveName.contains("minero")) {
            Block stone = Registries.BLOCK.get(new Identifier("minecraft:stone"));
            Stat<Block> minedStat = Stats.MINED.getOrCreateStat(stone);
            return player.getStatHandler().getStat(minedStat) >= 64;
        }
        if (objectiveName.contains("leñador")) {
            Block oakLog = Registries.BLOCK.get(new Identifier("minecraft:oak_log"));
            Block birchLog = Registries.BLOCK.get(new Identifier("minecraft:birch_log"));
            Block spruceLog = Registries.BLOCK.get(new Identifier("minecraft:spruce_log"));

            Stat<Block> oakStat = Stats.MINED.getOrCreateStat(oakLog);
            Stat<Block> birchStat = Stats.MINED.getOrCreateStat(birchLog);
            Stat<Block> spruceStat = Stats.MINED.getOrCreateStat(spruceLog);

            int logs = player.getStatHandler().getStat(oakStat) +
                    player.getStatHandler().getStat(birchStat) +
                    player.getStatHandler().getStat(spruceStat);
            return logs >= 32;
        }
        if (objectiveName.contains("arquero")) {
            Stat<Identifier> mobKillsStat = Stats.CUSTOM.getOrCreateStat(Stats.MOB_KILLS);
            return player.getStatHandler().getStat(mobKillsStat) >= 5;
        }
        if (objectiveName.contains("cazador")) {
            Stat<EntityType<?>> skeletonKillsStat = Stats.KILLED.getOrCreateStat(EntityType.SKELETON);
            return player.getStatHandler().getStat(skeletonKillsStat) >= 1;
        }
        if (objectiveName.contains("pescador")) {
            Stat<Identifier> fishCaughtStat = Stats.CUSTOM.getOrCreateStat(Stats.FISH_CAUGHT);
            return player.getStatHandler().getStat(fishCaughtStat) >= 3;
        }

        return false;
    }

    private boolean checkRoundCompletion(boolean[] progress) {
        for (boolean completed : progress) {
            if (!completed) return false;
        }
        return true;
    }

    private void startNextRound(ServerPlayerEntity player) {
        if (player == null || !playerProgress.containsKey(player)) {
            if (!playerProgress.isEmpty()) {
                player = playerProgress.keySet().iterator().next();
            } else {
                endGame();
                return;
            }
        }

        currentRound++;
        timeRemaining = ROUND_DURATIONS[currentRound];

        selectRandomObjectives();

        for (ServerPlayerEntity p : playerProgress.keySet()) {
            playerProgress.put(p, new boolean[OBJECTIVES_TO_SHOW]);
        }

        broadcastMessage("§6§l¡" + player.getName().getString() + " ha ganado la ronda " + currentRound + "!");
        broadcastMessage("§e¡Comienza la ronda " + (currentRound + 1) + "!");

        for (ServerPlayerEntity p : playerProgress.keySet()) {
            p.playSound(SoundEvents.ENTITY_PLAYER_LEVELUP, SoundCategory.PLAYERS, 1.0f, 1.0f);
            p.networkHandler.sendPacket(new TitleS2CPacket(Text.literal("§6§l¡NUEVA RONDA!")));
            p.networkHandler.sendPacket(new SubtitleS2CPacket(Text.literal("§eRonda " + (currentRound + 1))));
            setupScoreboard(p);
        }
    }


    private void announceWinner(ServerPlayerEntity winner, int score) {
        String message = String.format("§6§l¡%s ha ganado el BINGO con %d puntos!",
                winner.getName().getString(), score);

        for (ServerPlayerEntity player : playerProgress.keySet()) {
            player.networkHandler.sendPacket(new TitleS2CPacket(Text.literal("§6§l¡BINGO TERMINADO!")));
            player.networkHandler.sendPacket(new SubtitleS2CPacket(
                    Text.literal("§e¡" + winner.getName().getString() + " es el ganador!")));

            player.sendMessage(Text.literal(message));

            player.playSound(SoundEvents.UI_TOAST_CHALLENGE_COMPLETE,
                    SoundCategory.PLAYERS, 1.0f, 1.0f);

            player.getServerWorld().spawnParticles(
                    ParticleTypes.FIREWORK,
                    winner.getX(), winner.getY() + 1, winner.getZ(),
                    50, 0.5, 0.5, 0.5, 0.1
            );
        }
    }

    public void tick() {
        if (!isGameActive) return;

        timeRemaining--;

        if (timeRemaining % 20 == 0) { // Actualizar cada segundo
            for (ServerPlayerEntity player : playerProgress.keySet()) {
                setupScoreboard(player);

                if (timeRemaining == 1200) { // 1 minuto
                    player.sendMessage(Text.literal("§c¡Queda 1 minuto!"));
                } else if (timeRemaining == 600) { // 30 segundos
                    player.sendMessage(Text.literal("§c¡Quedan 30 segundos!"));
                } else if (timeRemaining == 200) { // 10 segundos
                    player.sendMessage(Text.literal("§c¡Quedan 10 segundos!"));
                }
            }
        }

        if (timeRemaining <= 0) {
            if (currentRound < ROUND_DURATIONS.length - 1) {
                ServerPlayerEntity roundWinner = null;
                int maxScore = -1;

                for (Map.Entry<ServerPlayerEntity, Integer> entry : playerScores.entrySet()) {
                    if (entry.getValue() > maxScore) {
                        maxScore = entry.getValue();
                        roundWinner = entry.getKey();
                    }
                }

                startNextRound(roundWinner != null ? roundWinner : playerProgress.keySet().iterator().next());
            } else {
                endGame();
            }
        }
    }

    private void endGame() {
        if (!isGameActive) return;

        isGameActive = false;

        ServerPlayerEntity winner = null;
        int maxScore = -1;

        for (Map.Entry<ServerPlayerEntity, Integer> entry : playerScores.entrySet()) {
            if (entry.getValue() > maxScore) {
                maxScore = entry.getValue();
                winner = entry.getKey();
            }
        }

        if (winner != null) {
            announceWinner(winner, maxScore);
        }

        for (ServerPlayerEntity player : playerProgress.keySet()) {
            player.sendMessage(Text.literal("§c¡El juego de Bingo ha terminado!"));

            Scoreboard scoreboard = player.getServer().getScoreboard();
            for (int i = 0; i < ROUND_DURATIONS.length; i++) {
                ScoreboardObjective objective = scoreboard.getObjective("bingo" + i);
                if (objective != null) {
                    scoreboard.removeObjective(objective);
                }
            }
        }

        showAllScores();

        // Limpiar datos
        playerProgress.clear();
        playerScores.clear();
        currentWinner = null;
        currentRound = 0;
    }

    private int countCompletedObjectives(boolean[] progress) {
        int count = 0;
        for (boolean completed : progress) {
            if (completed) count++;
        }
        return count;
    }

    private void updateScoreboard(ServerPlayerEntity player, int objectiveIndex) {
        setupScoreboard(player); // Actualizar todo el scoreboard
    }

    private static class BingoObjective {
        private final String name;
        private final Item requiredItem;
        private final String emoji;
        private final int points;

        public BingoObjective(String name, Item requiredItem, String description, int points) {
            this.name = name;
            this.requiredItem = requiredItem;
            this.emoji = name.split(" ")[0]; // El emoji está al inicio del nombre
            this.points = points;
        }

        public String getName() {
            return name;
        }

        public Item getRequiredItem() {
            return requiredItem;
        }

        public String getEmoji() {
            return emoji;
        }

        public int getPoints() {
            return points;
        }
    }
}