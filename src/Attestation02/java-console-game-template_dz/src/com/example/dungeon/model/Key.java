package com.example.dungeon.model;

public class Key extends Item {
    private final String forRoom;

    public Key(String name, String forRoom) {
        super(name);
        this.forRoom = forRoom;
    }

    public Key(String name) {
        this(name, "");
    }

    public String getForRoom() {
        return forRoom;
    }

    @Override
    public void apply(GameState ctx) {
        // Попытка использовать ключ для открытия двери в текущей комнате
        Room currentRoom = ctx.getCurrent();

        if (currentRoom.isLocked() && this.forRoom.equals(currentRoom.getName())) {
            currentRoom.setLocked(false);
            System.out.println("Вы использовали ключ '" + getName() + "' чтобы открыть дверь в " + currentRoom.getName() + "!");
            // Удаляем ключ из инвентаря после использования
            ctx.getPlayer().getInventory().remove(this);
        } else {
            System.out.println("Этот ключ не подходит для этой комнаты. Он для: " + forRoom);
        }
    }
}