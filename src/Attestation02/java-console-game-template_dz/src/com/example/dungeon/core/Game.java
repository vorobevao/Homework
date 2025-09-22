package com.example.dungeon.core;

import com.example.dungeon.model.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

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

        // Команда move
        commands.put("move", (ctx, a) -> {
            if (a.isEmpty()) throw new InvalidCommandException("move: требуется направление (north/south/east/west)");
            String dir = a.get(0).toLowerCase(Locale.ROOT);
            Room cur = ctx.getCurrent();
            Room next = cur.getNeighbors().get(dir);

            if (next == null) throw new InvalidCommandException("Нельзя идти " + dir + " отсюда.");

            // Проверка запертой двери
            if (next.isLocked()) {
                // Проверяем, есть ли у игрока нужный ключ
                boolean hasKey = ctx.getPlayer().getInventory().stream()
                        .anyMatch(item -> item instanceof Key &&
                                ((Key) item).getForRoom().equals(next.getName()));

                if (hasKey) {
                    // Находим ключ и используем его
                    Optional<Item> key = ctx.getPlayer().getInventory().stream()
                            .filter(item -> item instanceof Key &&
                                    ((Key) item).getForRoom().equals(next.getName()))
                            .findFirst();

                    if (key.isPresent()) {
                        // Используем ключ для открытия двери
                        next.setLocked(false);
                        System.out.println("Вы использовали ключ '" + key.get().getName() +
                                "' чтобы открыть дверь в " + next.getName() + "!");
                    }
                } else {
                    System.out.println("Дверь заперта! Нужен ключ: " + next.getRequiredKey());
                    return;
                }
            }

            ctx.setCurrent(next);
            System.out.println("Вы перешли в: " + next.getName());
            System.out.println(next.describe());
        });

        // Команда take
        commands.put("take", (ctx, a) -> {
            if (a.isEmpty()) throw new InvalidCommandException("Укажите название предмета");
            String itemName = String.join(" ", a);
            Room current = ctx.getCurrent();
            Optional<Item> item = current.getItems().stream()
                    .filter(i -> i.getName().equalsIgnoreCase(itemName))
                    .findFirst();

            if (item.isEmpty()) throw new InvalidCommandException("Предмет не найден: " + itemName);

            ctx.getPlayer().getInventory().add(item.get());
            current.getItems().remove(item.get());
            System.out.println("Взято: " + item.get().getName());
        });

        // Команда inventory с использованием Stream API
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

        // Команда use
        commands.put("use", (ctx, a) -> {
            if (a.isEmpty()) {
                throw new InvalidCommandException("Укажите название предмета");
            }

            String itemName = String.join(" ", a);
            Player player = ctx.getPlayer();
            Optional<Item> item = player.getInventory().stream()
                    .filter(i -> i.getName().equalsIgnoreCase(itemName))
                    .findFirst();

            if (item.isEmpty()) {
                throw new InvalidCommandException("Предмет не найден в инвентаре: " + itemName);
            }

            // Особенная обработка для ключей
            if (item.get() instanceof Key) {
                Key key = (Key) item.get();
                // Проверяем, находимся ли мы в комнате, для которой предназначен ключ
                if (ctx.getCurrent().getName().equals(key.getForRoom())) {
                    ctx.getCurrent().setLocked(false);
                    System.out.println("Вы использовали ключ '" + key.getName() + "' чтобы открыть дверь в этой комнате!");
                    player.getInventory().remove(key);
                } else {
                    System.out.println("Этот ключ не подходит для этой комнаты. Он для: " + key.getForRoom());
                }
            } else {
                // Стандартная обработка для других предметов
                item.get().apply(ctx);
            }
        });

        // Команда fight
        commands.put("fight", (ctx, a) -> {
            Room current = ctx.getCurrent();
            if (current.getMonster() == null) throw new InvalidCommandException("В комнате нет монстров");

            Monster monster = current.getMonster();
            Player player = ctx.getPlayer();

            System.out.println("Бой с " + monster.getName() + "ом" + " начинается!");

            while (player.getHp() > 0 && monster.getHp() > 0) {
                // Игрок атакует
                int playerDamage = player.getAttack();
                monster.setHp(monster.getHp() - playerDamage);
                System.out.println("Вы бьёте " + monster.getName() + "а" + " на " + playerDamage +
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
                // Сохраняем результат перед выходом
                SaveLoad.writeScore(player.getName(), ctx.getScore());
                System.exit(0);
            } else {
                System.out.println("Вы победили " + monster.getName() + "а" + "!");
                // Добавляем лут за победу
                current.getItems().add(new Potion("Зелье здоровья", 10));
                current.setMonster(null);
            }
        });

        // Команда save
        commands.put("save", (ctx, a) -> SaveLoad.save(ctx));

        // Команда load
        commands.put("load", (ctx, a) -> SaveLoad.load(ctx));

        // Команда scores
        commands.put("scores", (ctx, a) -> SaveLoad.printScores());

        // Команда exit с сохранением результатов
        commands.put("exit", (ctx, a) -> {
            SaveLoad.writeScore(ctx.getPlayer().getName(), ctx.getScore());
            System.out.println("Пока! Ваш результат сохранен.");
            System.exit(0);
        });

        // Команда alloc для демонстрации работы GC
        commands.put("alloc", (ctx, a) -> {
            System.out.println("Создаем объекты для демонстрации работы GC...");
            List<String> objects = new ArrayList<>();
            for (int i = 0; i < 100000; i++) {
                objects.add("Object-" + i);
            }
            System.out.println("Создано 100000 объектов. Память до очистки:");
            printMemoryStats();

            // Освобождаем ссылки для демонстрации работы GC
            objects = null;
            System.gc();

            try {
                Thread.sleep(1000); // Даем время GC поработать
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            System.out.println("Память после очистки:");
            printMemoryStats();
        });

        // Демонстрация ошибок (закомментировано)
        /*
        // Пример ошибки компиляции (раскомментируйте для проверки)
        // String s = 5; // Несовместимые типы: int нельзя присвоить String

        // Пример ошибки выполнения (раскомментируйте для проверки)
        // int result = 10 / 0; // ArithmeticException: division by zero
        */
    }

    private void printMemoryStats() {
        Runtime rt = Runtime.getRuntime();
        long free = rt.freeMemory(), total = rt.totalMemory(), used = total - free;
        System.out.println("Память: used=" + used + " free=" + free + " total=" + total);
    }

    private void bootstrapWorld() {
        Player hero = new Player("Герой", 20, 5);
        state.setPlayer(hero);

        Room square = new Room("Площадь", "Каменная площадь с фонтаном.");
        Room forest = new Room("Лес", "Шелест листвы и птичий щебет.");
        Room cave = new Room("Пещера", "Темно и сыро.");
        Room treasure = new Room("Сокровищница", "Комната, полная сокровищ!", true, "Золотой ключ");

        // Настраиваем связи между комнатами
        square.getNeighbors().put("north", forest);
        forest.getNeighbors().put("south", square);
        forest.getNeighbors().put("east", cave);
        cave.getNeighbors().put("west", forest);
        cave.getNeighbors().put("north", treasure); // Закрытый проход в сокровищницу
        treasure.getNeighbors().put("south", cave);

        // Добавляем предметы
        forest.getItems().add(new Potion("Малое зелье", 5));
        forest.setMonster(new Monster("Волк", 1, 8));

        // Добавляем ключ от сокровищницы в пещеру
        cave.getItems().add(new Key("Золотой ключ", "Сокровищница"));
        cave.setMonster(new Monster("Гоблин", 2, 12));

        // Добавляем сокровища в сокровищницу
        treasure.getItems().add(new Weapon("Драгоценный меч", 10));
        treasure.getItems().add(new Potion("Эликсир жизни", 20));
        treasure.setMonster(new Monster("Дракон", 3, 30));

        // Сохраняем все комнаты для сериализации
        List<Room> allRooms = Arrays.asList(square, forest, cave, treasure);
        state.setAllRooms(allRooms);
        state.setCurrent(square);
    }

    public void run() {
        System.out.println("DungeonMini (Добро пожаловать). 'help' — команды.");
        try (BufferedReader in = new BufferedReader(new InputStreamReader(System.in))) {
            while (true) {
                System.out.print("> ");
                String line = in.readLine();
                if (line == null) break;
                line = line.trim();
                if (line.isEmpty()) continue;
                List<String> parts = Arrays.asList(line.split("\\s+"));
                String cmd = parts.get(0).toLowerCase(Locale.ROOT);
                List<String> args = parts.subList(1, parts.size());
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
        } catch (IOException e) {
            System.out.println("Ошибка ввода/вывода: " + e.getMessage());
        }
    }
}