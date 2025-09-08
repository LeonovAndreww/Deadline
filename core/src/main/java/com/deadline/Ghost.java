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

            sndAttack = Gdx.audio.newSound(Gdx.files.internal("sounds/ghostAttack.mp3"));
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
            if (isAlive()) {
//                if (getBody().getUserData()=="hit") {
//                    hit(damage);
//                    getBody().setUserData("ghost");
//                }
            }
        }

        public void attack(Vector2 playerPos) {
            if (isBattle) {
                if (TimeUtils.millis() - timeLastAttack > weapon.getReloadTime()+random.nextInt(2550)) {
                    getBody().setLinearVelocity(playerPos.sub(getPosition()).nor().scl(55+random.nextInt(35)));
                    sndAttack.play(0.75f * soundVolume);
                    timeLastAttack = TimeUtils.millis();
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
