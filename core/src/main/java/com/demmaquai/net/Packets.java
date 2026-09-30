package com.demmaquai.net;

import java.util.ArrayList;

public class Packets {
    public static class Join { public String name; }
    public static class Input {
        public float dx, dy;
        public float yaw, pitch;
        public boolean firing;
    }
    public static class PlayerState {
        public int id;
        public float x, y, z;
        public float yaw;
        public int hp;
    }
    public static class World {
        public ArrayList<PlayerState> players = new ArrayList<>();
        public int yourId;
    }
}
