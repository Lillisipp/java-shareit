package ru.practicum.shareit.utils;

import ru.practicum.shareit.item.Item;

public class Utils {
    boolean contains(String s, String q) {
        return s != null && s.toLowerCase().contains(q);
    }

    Item copy(Item src) {
        Item c = new Item();
        c.setId(src.getId());
        c.setName(src.getName());
        c.setDescription(src.getDescription());
        c.setAvailable(src.getAvailable());
        c.setOwner(src.getOwner());          // тут можно тоже делать тонкую копию, если нужно
        c.setRequest(src.getRequest());
        return c;
    }
}
