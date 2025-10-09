    package com.deadline;

    import static com.deadline.DdlnGame.soundVolume;

    import com.badlogic.gdx.Gdx;
    import com.badlogic.gdx.audio.Sound;
    import com.badlogic.gdx.math.Vector2;
    import com.badlogic.gdx.physics.box2d.World;
    import com.badlogic.gdx.utils.TimeUtils;

    import java.util.Random;

    public class Ghost extends Entity{
        private final World world;
        protected long timePhaseInterval;
        private Weapon weapon;
        private long timeLastAttack, timeLastPhase;
        private int phase, nPhases;
        private int room;
        private final Sound sndAttack;
        private float attackOffset;
        private float speedOffset;
        Random random = new Random();
        public Ghost(World world, float width, float height, float x, float y, int maxHealth, int nPhases, long timePhaseInterval, Weapon weapon) {
            super(world, width, height, x, y, maxHealth, nPhases, timePhaseInterval);
            this.world = world;
            this.nPhases = nPhases;
            this.timePhaseInterval = timePhaseInterval+random.nextInt(101);
            this.weapon = weapon;
            isBattle = false;
            this.phase = random.nextInt(4);
            getBody().setLinearDamping(0.75f);
            getBody().setUserData("ghost");
            attackOffset = 1000 + random.nextInt(500);
            speedOffset = random.nextInt(25);

            sndAttack = Gdx.audio.newSound(Gdx.files.internal("sounds/ghostAttack.ogg"));
        }

        @Override
        public void changePhase() {
            if (isBattle) {
                long interval = timePhaseInterval;
                if (TimeUtils.millis() > timeLastPhase + interval) {
                    if (++phase == nPhases) phase = 0;
                    timeLastPhase = TimeUtils.millis();
                }
            }
        }


        public void update() {
            // I should make it the same way as in the Zombie.java
        }

        public void attack(Vector2 playerPos) {
            if (isBattle) {
                if (TimeUtils.millis() - timeLastAttack > weapon.getReloadTime() + attackOffset) {
                    getBody().setLinearVelocity(playerPos.sub(getPosition()).nor().scl(75f + speedOffset));
                    sndAttack.play(0.75f * soundVolume);
                    timeLastAttack = TimeUtils.millis();
                    attackOffset = random.nextInt(450);
                    speedOffset = random.nextInt(55);
                }
            }
        }

        public int getRoom() {
            return room;
        }

        public void setRoomNum(int room) {
            this.room = room;
        }

        public void setBattle(boolean battle) {
            this.isBattle = battle;
        }

        @Override
        public int getPhase() {
            return phase;
        }

        public void dispose() {
            sndAttack.dispose();
        }
    }
