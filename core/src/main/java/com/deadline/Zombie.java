    package com.deadline;

    import com.badlogic.gdx.math.Vector2;
    import com.badlogic.gdx.physics.box2d.World;
    import com.badlogic.gdx.utils.TimeUtils;

    import java.util.Random;

    public class Zombie extends Entity{
        private final World world;
        protected long timePhaseInterval;
        private final Weapon weapon;
        private long timeLastAttack, timeLastPhase;
        private int phase, nPhases;
        private int room;
        private float speedOffset;
//        private final Sound sndAttack;
        Random random = new Random();

        public Zombie(World world, float width, float height, float x, float y, int maxHealth, int nPhases, long timePhaseInterval, Weapon weapon) {
            super(world, width, height, x, y, maxHealth, nPhases, timePhaseInterval);
            this.world = world;
            this.nPhases = nPhases;
            this.timePhaseInterval = timePhaseInterval+random.nextInt(101);
            this.weapon = weapon;
            isBattle = false;
            this.phase = random.nextInt(4);
            getBody().setUserData("zombie");
            speedOffset = random.nextInt(5);

//            sndAttack = Gdx.audio.newSound(Gdx.files.internal("zombieAttack.ogg"));
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
                if (Math.abs(getBody().getLinearVelocity().x) > Math.abs(getBody().getLinearVelocity().y)) {
                    if (getBody().getLinearVelocity().x > 0) setDirection('r');
                    else setDirection('l');
                } else {
                    if (getBody().getLinearVelocity().y > 0) setDirection('u');
                    else setDirection('d');
                }
            }
        }

        public void attack(Vector2 playerPos) {
            if (isBattle) {
                getBody().setLinearVelocity(playerPos.sub(getPosition()).nor().scl(15+ speedOffset));
//                    sndAttack.play(); // if touched, not if attacked
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
//            sndAttack.dispose();
        }
    }
