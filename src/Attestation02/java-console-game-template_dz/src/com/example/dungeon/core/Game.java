package com.example.dungeon.core;

import com.example.dungeon.model.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;
import java.io.PrintStream;

public class Game {
    private final GameState state = new GameState();
    private final Map<String, Command> commands = new LinkedHashMap<>();

    static {
        WorldInfo.touch("Game");
    }

    public Game() {
        registerCommands();
        bootstrapWorld();
    }

    private void registerCommands() {
        commands.put("help", (ctx, a) -> System.out.println("Команды: " + String.join(", ", commands.keySet())));

        commands.put("about", (ctx, a) -> {
            System.out.println("DungeonMini Game v1.0");
            System.out.println("Текстовая RPG игра с исследованием подземелий,");
            System.out.println("сражениями с монстрами и сбором предметов.");
            System.out.println("Разработана на Java с использованием Stream API.");
            System.out.println("Доступные команды: look, move, take, use, fight,");
            System.out.println("inventory, save, load, scores, gc-stats, about, exit");
        });

        commands.put("gc-stats", (ctx, a) -> {
            Runtime rt = Runtime.getRuntime();
            long free = rt.freeMemory(), total = rt.totalMemory(), used = total - free;
            System.out.println("Память: used=" + used + " free=" + free + " total=" + total);
        });

        commands.put("look", (ctx, a) -> System.out.println(ctx.getCurrent().describe()));

        commands.put("move", (ctx, a) -> {
            if (a.isEmpty()) {
                throw new InvalidCommandException("Укажите направление: north, south, east, west");
            }
            String direction = a.get(0).toLowerCase();
            Room currentRoom = ctx.getCurrent();
            Room nextRoom = currentRoom.getNeighbors().get(direction);

            if (nextRoom == null) {
                throw new InvalidCommandException("Нет пути в направлении: " + direction);
            }

            // Проверяем, заблокирована ли комната
            if (nextRoom.isLocked()) {
                // Проверяем, есть ли у игрока нужный ключ
                Player player = ctx.getPlayer();
                boolean hasKey = player.getInventory().stream()
                        .anyMatch(item -> item instanceof Key &&
                                ((Key) item).getForRoom().equals(nextRoom.getName()));

                if (hasKey) {
                    // Находим ключ и используем его
                    Optional<Item> key = player.getInventory().stream()
                            .filter(item -> item instanceof Key &&
                                    ((Key) item).getForRoom().equals(nextRoom.getName()))
                            .findFirst();

                    if (key.isPresent()) {
                        key.get().apply(ctx); // Используем ключ для открытия двери
                    }
                } else {
                    throw new InvalidCommandException("Дверь в " + nextRoom.getName() + " закрыта. Нужен ключ: " + nextRoom.getRequiredKey());
                }
            }

            // Переходим в следующую комнату
            ctx.setCurrent(nextRoom);
            System.out.println("Вы перешли в: " + nextRoom.getName());
            System.out.println(nextRoom.describe());
        });

        commands.put("take", (ctx, a) -> {


            // ПРОСТО ВЫВЕДЕМ ПЕРВЫЙ АРГУМЕНТ КАК ЕСТЬ
            if (!a.isEmpty()) {
                String firstArg = a.get(0);
                System.out.println("First argument: '" + firstArg + "'");
                System.out.println("First argument length: " + firstArg.length());

                // Выводим каждый символ аргумента
                System.out.print("Argument characters: ");
                for (int i = 0; i < firstArg.length(); i++) {
                    char c = firstArg.charAt(i);
                    System.out.print("['" + c + "'=" + (int)c + "] ");
                }
                System.out.println();
            }

            Room currentRoom = ctx.getCurrent();


            // ПРОСТО ВОЗЬМЕМ ПЕРВЫЙ ПРЕДМЕТ БЕЗ ПРОВЕРОК
            if (!currentRoom.getItems().isEmpty()) {
                Item firstItem = currentRoom.getItems().get(0);
                ctx.getPlayer().getInventory().add(firstItem);
                currentRoom.getItems().remove(firstItem);
                System.out.println("Взято: " + firstItem.getName());
            } else {
                System.out.println("В комнате нет предметов");
            }

        });

        commands.put("inventory", (ctx, a) -> {
            Player player = ctx.getPlayer();
            if (player.getInventory().isEmpty()) {
                System.out.println("Инвентарь пуст");
                return;
            }

            player.getInventory().stream()
                    .collect(Collectors.groupingBy(
                            item -> item.getClass().getSimpleName(),
                            Collectors.mapping(Item::getName, Collectors.toList())
                    ))
                    .forEach((type, names) -> {
                        Collections.sort(names);
                        System.out.println("- " + type + " (" + names.size() + "): " + String.join(", ", names));
                    });
        });

        commands.put("use", (ctx, a) -> {

            if (!a.isEmpty()) {
                String firstArg = a.get(0);
                System.out.println("First argument: '" + firstArg + "'");
                System.out.println("First argument length: " + firstArg.length());

                // Выводим каждый символ аргумента
                System.out.print("Argument characters: ");
                for (int i = 0; i < firstArg.length(); i++) {
                    char c = firstArg.charAt(i);
                    System.out.print("['" + c + "'=" + (int)c + "] ");
                }
                System.out.println();
            }

            Player player = ctx.getPlayer();


            if (!player.getInventory().isEmpty()) {
                Item firstItem = player.getInventory().get(0);
                System.out.println("Используем: " + firstItem.getName());

                // Применяем предмет
                firstItem.apply(ctx);

                // Удаляем предмет из инвентаря после использования (если он не был удален в apply)
                if (player.getInventory().contains(firstItem)) {
                    player.getInventory().remove(firstItem);
                }
            } else {
                System.out.println("Инвентарь пуст");
            }
        });

        commands.put("fight", (ctx, a) -> {
            Room currentRoom = ctx.getCurrent();
            if (currentRoom.getMonster() == null) {
                throw new InvalidCommandException("В комнате нет монстров");
            }

            Monster monster = currentRoom.getMonster();
            Player player = ctx.getPlayer();

            System.out.println("Бой с " + monster.getName()+ "ом " + " начинается!");

            while (player.getHp() > 0 && monster.getHp() > 0) {
                // Игрок атакует
                int playerDamage = player.getAttack();
                monster.setHp(monster.getHp() - playerDamage);
                System.out.println("Вы бьёте " + monster.getName()+ "а " + " на " + playerDamage +
                        ". HP монстра: " + Math.max(0, monster.getHp()));

                if (monster.getHp() <= 0) break;

                // Монстр атакует
                int monsterDamage = monster.getLevel();
                player.setHp(player.getHp() - monsterDamage);
                System.out.println("Монстр отвечает на " + monsterDamage +
                        ". Ваше HP: " + Math.max(0, player.getHp()));
            }

            if (player.getHp() <= 0) {
                System.out.println("Вы погибли! Игра окончена.");
                SaveLoad.writeScore(player.getName(), ctx.getScore());
                System.exit(0);
            } else {
                System.out.println("Вы победили " + monster.getName() + "!");
                currentRoom.getItems().add(new Potion("Зелье здоровья", 10));
                currentRoom.setMonster(null);
            }
        });

        commands.put("save", (ctx, a) -> SaveLoad.save(ctx));
        commands.put("load", (ctx, a) -> SaveLoad.load(ctx));
        commands.put("scores", (ctx, a) -> SaveLoad.printScores());
        commands.put("exit", (ctx, a) -> {
            SaveLoad.writeScore(ctx.getPlayer().getName(), ctx.getScore());
            System.out.println("Пока! Ваш результат сохранен.");
            System.exit(0);
        });
    }

    private void bootstrapWorld() {
        Player hero = new Player("Герой", 20, 5);
        state.setPlayer(hero);

        Room square = new Room("Площадь", "Каменная площадь с фонтаном.");
        Room forest = new Room("Лес", "Шелест листвы и птичий щебет.");
        Room cave = new Room("Пещера", "Темно и сыро.");
        Room treasure = new Room("Сокровищница", "Комната, полная сокровищ!", true, "Золотой ключ");

        square.getNeighbors().put("north", forest);
        forest.getNeighbors().put("south", square);
        forest.getNeighbors().put("east", cave);
        cave.getNeighbors().put("west", forest);
        cave.getNeighbors().put("north", treasure);
        treasure.getNeighbors().put("south", cave);

        forest.getItems().add(new Potion("Малое зелье", 5));
        forest.setMonster(new Monster("Волк", 1, 8));

        cave.getItems().add(new Key("Золотой ключ", "Сокровищница"));
        cave.setMonster(new Monster("Гоблин", 2, 12));

        treasure.getItems().add(new Weapon("Драгоценный меч", 10));
        treasure.getItems().add(new Potion("Эликсир жизни", 20));
        treasure.setMonster(new Monster("дракон", 3, 15));

        List<Room> allRooms = Arrays.asList(square, forest, cave, treasure);
        state.setAllRooms(allRooms);
        state.setCurrent(square);
    }

    public void run() {
        System.out.println("DungeonMini Game. 'help' — команды.");

        Scanner scanner = new Scanner(System.in, getConsoleEncoding());

        while (true) {
            System.out.print("> ");
            String line = scanner.nextLine().trim();
            if (line.isEmpty()) continue;

            // Автоматическое исправление русских символов
            line = fixRussianCharacters(line);

            // Разбиваем на команду и аргументы
            String[] parts = line.split(" ", 2);
            String cmd = parts[0].toLowerCase();
            List<String> args = new ArrayList<>();

            if (parts.length > 1) {
                args.add(parts[1].trim());
            }

            Command c = commands.get(cmd);
            try {
                if (c == null) throw new InvalidCommandException("Неизвестная команда: " + cmd);
                c.execute(state, args);
                state.addScore(1);
            } catch (InvalidCommandException e) {
                System.out.println("Ошибка: " + e.getMessage());
            } catch (Exception e) {
                System.out.println("Непредвиденная ошибка: " + e.getClass().getSimpleName() + ": " + e.getMessage());
            }
        }
    }

    private String getConsoleEncoding() {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("windows")) {
            return "CP1251";
        } else {
            return "UTF-8";
        }
    }

    private String fixRussianCharacters(String input) {

        if (input.contains("�")) {
            try {
                // Пробуем преобразовать из CP1251 в UTF-8
                byte[] bytes = input.getBytes("CP1251");
                return new String(bytes, "UTF-8");
            } catch (Exception e) {

                return input;
            }
        }
        return input;
    }
    public static void main(String[] args) {
        new Game().run();
    }
}