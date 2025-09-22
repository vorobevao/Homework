package com.example.dungeon.core;

import com.example.dungeon.model.*;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class SaveLoad {
    private static final Path SAVE = Paths.get("save.txt");
    private static final Path SCORES = Paths.get("scores.csv");

    public static void save(GameState s) {
        try (BufferedWriter w = Files.newBufferedWriter(SAVE)) {
            Player p = s.getPlayer();
            w.write("player;" + p.getName() + ";" + p.getHp() + ";" + p.getAttack());
            w.newLine();


            String inv = p.getInventory().stream()
                    .map(i -> {
                        if (i instanceof Key) {
                            Key key = (Key) i;
                            return i.getClass().getSimpleName() + ":" + i.getName() + ":" + key.getForRoom();
                        } else {
                            return i.getClass().getSimpleName() + ":" + i.getName();
                        }
                    })
                    .collect(Collectors.joining(","));



            w.write("inventory;" + inv);
            w.newLine();
            System.out.println("Сохранено в " + SAVE.toAbsolutePath());


        } catch (IOException e) {
            throw new UncheckedIOException("Не удалось сохранить игру", e);
        }
    }

    public static void load(GameState s) {
        if (!Files.exists(SAVE)) {
            System.out.println("Сохранение не найдено.");
            return;
        }
        try (BufferedReader r = Files.newBufferedReader(SAVE)) {
            Map<String, String> map = new HashMap<>();
            for (String line; (line = r.readLine()) != null; ) {
                String[] parts = line.split(";", 2);
                if (parts.length == 2) map.put(parts[0], parts[1]);
            }

            Player p = s.getPlayer();
            String[] pp = map.getOrDefault("player", "player;Hero;10;3").split(";");
            if (pp.length >= 4) {
                p.setName(pp[1]);
                p.setHp(Integer.parseInt(pp[2]));
                p.setAttack(Integer.parseInt(pp[3]));
            }

            p.getInventory().clear();
            String inv = map.getOrDefault("inventory", "");
            if (!inv.isBlank()) {
                for (String tok : inv.split(",")) {
                    String[] t = tok.split(":", 3);
                    if (t.length >= 2) {
                        switch (t[0]) {
                            case "Potion" -> p.getInventory().add(new Potion(t[1], 5));
                            case "Key" -> {
                                if (t.length >= 3) {
                                    p.getInventory().add(new Key(t[1], t[2]));
                                } else {
                                    p.getInventory().add(new Key(t[1]));
                                }
                            }
                            case "Weapon" -> p.getInventory().add(new Weapon(t[1], 3));
                            default -> {}
                        }
                    }
                }
            }


            String roomsData = map.getOrDefault("rooms", "");
            if (!roomsData.isBlank()) {
                String[] roomsInfo = roomsData.split(";");
                for (String roomInfo : roomsInfo) {
                    if (roomInfo.isEmpty()) continue;

                    String[] parts = roomInfo.split(":", 6);
                    if (parts.length >= 3) {
                        String roomName = parts[0];
                        boolean isLocked = Boolean.parseBoolean(parts[1]);
                        String requiredKey = "null".equals(parts[2]) ? null : parts[2];

                        // Находим комнату по имени
                        Optional<Room> roomOpt = s.getAllRooms().stream()
                                .filter(room -> room.getName().equals(roomName))
                                .findFirst();

                        if (roomOpt.isPresent()) {
                            Room room = roomOpt.get();
                            room.setLocked(isLocked);
                            room.setRequiredKey(requiredKey);

                            // Восстанавливаем монстра (если есть)
                            if (parts.length >= 5 && !"null".equals(parts[3])) {
                                String monsterName = parts[3];
                                int monsterHp = Integer.parseInt(parts[4]);
                                int monsterLevel = Integer.parseInt(parts[5]);
                                room.setMonster(new Monster(monsterName, monsterLevel, monsterHp));
                            } else {
                                room.setMonster(null);
                            }

                            // Восстанавливаем предметы в комнате (если есть)
                            if (parts.length >= 6) {
                                room.getItems().clear();
                                String itemsData = parts[5];
                                if (!itemsData.isEmpty()) {
                                    for (String itemStr : itemsData.split(",")) {
                                        String[] itemParts = itemStr.split("\\|", 3);
                                        if (itemParts.length >= 2) {
                                            switch (itemParts[0]) {
                                                case "Potion" -> room.getItems().add(new Potion(itemParts[1], 5));
                                                case "Key" -> {
                                                    if (itemParts.length == 3) {
                                                        room.getItems().add(new Key(itemParts[1], itemParts[2]));
                                                    } else {
                                                        room.getItems().add(new Key(itemParts[1]));
                                                    }
                                                }
                                                case "Weapon" -> room.getItems().add(new Weapon(itemParts[1], 3));
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }


            String currentRoomName = map.getOrDefault("room", "");
            if (!currentRoomName.isBlank()) {
                Optional<Room> currentRoom = s.getAllRooms().stream()
                        .filter(room -> room.getName().equals(currentRoomName))
                        .findFirst();
                currentRoom.ifPresent(s::setCurrent);
            }

            String scoreStr = map.getOrDefault("score", "0");
            s.setScore(Integer.parseInt(scoreStr));

            System.out.println("Игра загружена.");
        } catch (IOException e) {
            throw new UncheckedIOException("Не удалось загрузить игру", e);
        }
    }

    public static void printScores() {
        if (!Files.exists(SCORES)) {
            System.out.println("Пока нет результатов.");
            return;
        }
        try (BufferedReader r = Files.newBufferedReader(SCORES)) {
            System.out.println("Таблица лидеров (топ-10):");
            r.lines().skip(1).map(l -> l.split(","))
                    .filter(a -> a.length >= 3)
                    .map(a -> new Score(a[1], Integer.parseInt(a[2])))
                    .sorted(Comparator.comparingInt(Score::score).reversed())
                    .limit(10)
                    .forEach(s -> System.out.println(s.player() + " — " + s.score()));
        } catch (IOException e) {
            System.err.println("Ошибка чтения результатов: " + e.getMessage());
        }
    }

    public static void writeScore(String player, int score) {
        try {
            boolean header = !Files.exists(SCORES);
            try (BufferedWriter w = Files.newBufferedWriter(SCORES,
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
                if (header) {
                    w.write("ts,player,score");
                    w.newLine();
                }
                w.write(LocalDateTime.now() + "," + player + "," + score);
                w.newLine();
            }
        } catch (IOException e) {
            System.err.println("Не удалось записать очки: " + e.getMessage());
        }
    }

    private record Score(String player, int score) {}
}