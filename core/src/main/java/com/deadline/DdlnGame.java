package com.deadline;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;

public class DdlnGame extends Game {

	public static final float SCR_WIDTH = 240, SCR_HEIGHT = 135;
	public static final int THICKNESS = 10;

	public SpriteBatch batch;
	public OrthographicCamera camera;
	public Vector3 touch;
	public BitmapFont font, fontUi;

	public static GlyphLayout glyphLayout;

	ScreenMenu screenMenu;
	ScreenGame screenGame;
	ScreenEnding screenEnding;

	static float ambientLight = 0.375f;
	static int playerLightDistance = 135;
	static float musicVolume, soundVolume;
	static boolean isMusicOn, isSoundOn;

	@Override
	public void create () {
		soundVolume = 1;
		musicVolume = 0.75f;
		isMusicOn = true;
		isSoundOn = true;

		batch = new SpriteBatch();
		camera = new OrthographicCamera();
		camera.setToOrtho(false, SCR_WIDTH, SCR_HEIGHT);
		touch = new Vector3();
		fontGenerate();

		screenMenu = new ScreenMenu(this);
		screenGame = new ScreenGame(this);
		screenEnding = new ScreenEnding(this);
		setScreen(screenMenu);
	}

	@Override
	public void dispose () {
		batch.dispose();
		font.dispose();
		fontUi.dispose();
	}

//	private void fontGenerate() {
//		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/font.ttf"));
//		FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
//		parameter.characters = FONT_CHARS;
//		parameter.size = 10;
//		parameter.color = Color.BLACK;
//		font = generator.generateFont(parameter);
//		parameter.size = 10;
//		parameter.color = Color.WHITE;
//		fontUi = generator.generateFont(parameter);
//		generator.dispose();
//	}

	private void fontGenerate() {
		font = new BitmapFont(Gdx.files.internal("fonts/font.fnt"));
		fontUi = new BitmapFont(Gdx.files.internal("fonts/font.fnt"));

		font.setColor(0, 0, 0, 1);
		font.getData().setScale(0.25f);
		fontUi.getData().setScale(0.25f);
	}
}
