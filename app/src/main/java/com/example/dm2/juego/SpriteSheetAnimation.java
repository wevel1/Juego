package com.example.dm2.juego;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

public class SpriteSheetAnimation extends Activity {

    GameView gameView;
    int pasos=0,sw=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Toast.makeText(this,"Pulsa o manten pulsada la pantalla para moverte por el eje x",Toast.LENGTH_LONG);
        gameView = new GameView(this);
        setContentView(gameView);

    }

    class GameView extends SurfaceView implements Runnable {

        Thread gameThread = null;

        SurfaceHolder ourHolder;

        volatile boolean playing;

        Canvas canvas;
        Paint paint;

        long fps;

        private long timeThisFrame;

        Bitmap bitmapBob;

        boolean enMovimiento = false;

        float VelocidadDeMovimiento = 250;

        float EjeX = 10;

        private int frameWidth = 100;
        private int frameHeight = 50;

        private int frameCount = 5;

        private int currentFrame = 0;

        private long lastFrameChangeTime = 0;

        private int frameLengthInMilliseconds = 100;

        private Rect frameToDraw = new Rect(
                0,
                0,
                frameWidth,
                frameHeight);

        RectF whereToDraw = new RectF(
                EjeX,                0,
                EjeX + frameWidth,
                frameHeight);

        public GameView(Context context) {
            super(context);

            ourHolder = getHolder();
            paint = new Paint();

            bitmapBob = BitmapFactory.decodeResource(this.getResources(), R.drawable.bob);

            bitmapBob = Bitmap.createScaledBitmap(bitmapBob,
                    frameWidth * frameCount,
                    frameHeight,
                    false);


        }

        @Override
        public void run() {
            while (playing) {

                long startFrameTime = System.currentTimeMillis();

                update();

                draw();

                timeThisFrame = System.currentTimeMillis() - startFrameTime;
                if (timeThisFrame >= 1) {
                    fps = 1000 / timeThisFrame;
                }

            }

        }

        public void update() {

            if(enMovimiento){
                if(pasos > 60)
                {
                    sw=1;
                }
                if(pasos < 1)
                {
                   sw=0;
                }
                if(sw==0) {
                    EjeX = EjeX + (VelocidadDeMovimiento / fps);
                    pasos++;
                }
                else
                {
                    EjeX = EjeX - (VelocidadDeMovimiento / fps);
                    pasos--;
                }
            }

        }

        public void getCurrentFrame(){

            long time  = System.currentTimeMillis();
            if(enMovimiento) {
                if ( time > lastFrameChangeTime + frameLengthInMilliseconds) {
                    lastFrameChangeTime = time;
                    currentFrame++;
                    if (currentFrame >= frameCount) {

                        currentFrame = 0;
                    }
                }
            }
            frameToDraw.left = currentFrame * frameWidth;
            frameToDraw.right = frameToDraw.left + frameWidth;

        }

        public void draw() {

            if (ourHolder.getSurface().isValid()) {
                canvas = ourHolder.lockCanvas();

                canvas.drawColor(Color.argb(255,  26, 128, 182));

                paint.setColor(Color.argb(255,  249, 129, 0));

                paint.setTextSize(45);

                canvas.drawText("FPS:" + fps, 20, 40, paint);


                whereToDraw.set((int) EjeX,
                        0,
                        (int) EjeX + frameWidth,
                        frameHeight);

                getCurrentFrame();

                canvas.drawBitmap(bitmapBob,
                        frameToDraw,
                        whereToDraw, paint);

                ourHolder.unlockCanvasAndPost(canvas);
            }

        }

        public void pause() {
            playing = false;
            try {
                gameThread.join();
            } catch (InterruptedException e) {
                Log.e("Error:", "joining thread");
            }

        }

        public void resume() {
            playing = true;
            gameThread = new Thread(this);
            gameThread.start();
        }

        @Override
        public boolean onTouchEvent(MotionEvent motionEvent) {

            switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {

                case MotionEvent.ACTION_DOWN:

                    enMovimiento = true;

                    break;

                case MotionEvent.ACTION_UP:

                    enMovimiento = false;

                    break;
            }
            return true;
        }

    }
    @Override
    protected void onResume() {
        super.onResume();

        gameView.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();

        gameView.pause();
    }

}