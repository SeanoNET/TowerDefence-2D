import javax.swing.*;
import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.net.URL;

public class Screen extends JPanel implements Runnable{
	public Thread thread = new Thread(this);
	
	public static Image[] tileset_groundGrass = new Image[100];
	public static Image[] tileset_air = new Image[100];
	public static Image[] tileset_res = new Image[100];
	public static Image[] tileset_mob = new Image[100];
	public static Image[] tileset_boss = new Image[100];
	
	public static int myWidth, myHeight; 
	public static boolean isFirst = true;
	
	public static Boolean isDebug = false; //DEBUG mode
	public static int killCount = 0;
	
	public static Room room;
	public static Save save;
	public static Point mse = new Point(0,0);
	public static Store store;
	public static Mob[] mobs = new Mob[100];

	
	
	public int mobNumber = Screen.level * 3;
	public int mobSpawned = 0;
	
	public static int coinage =25, health =100;
	
	public static int level =1;
	
	public Screen(Frame frame) {
		frame.addMouseListener(new KeyHandel());
		frame.addMouseMotionListener(new KeyHandel());
		
		thread.start();
		
	}
	public void define(){
		room = new Room();
		save = new Save();
		store = new Store();

		
		URL groundGrassLoc = getClass().getResource("Textures/Tile_GroundGrass.png");
		for(int i =0; i< tileset_groundGrass.length;i++){
			tileset_groundGrass[i] = new ImageIcon(groundGrassLoc).getImage();
			tileset_groundGrass[i] = createImage(new FilteredImageSource(tileset_groundGrass[i].getSource(), new CropImageFilter(0,25*i,25,25)));
		}
		URL airAirLoc = getClass().getResource("Textures/Tile_airAir.png");
		for(int i =0; i< tileset_air.length;i++){
			tileset_air[i] = new ImageIcon(airAirLoc).getImage();
			tileset_air[i] = createImage(new FilteredImageSource(tileset_air[i].getSource(), new CropImageFilter(0,25*i,25,25)));
		}
		
		URL cellLoc = getClass().getResource("Textures/Cell.png");
		tileset_res[0] = new ImageIcon(cellLoc).getImage();
		
		URL coinLoc = getClass().getResource("Textures/coin.png");
		tileset_res[1] = new ImageIcon(coinLoc).getImage();
		
		URL heartLoc = getClass().getResource("Textures/heart.png");
		tileset_res[2] = new ImageIcon(heartLoc).getImage();
		
		URL mobLoc = getClass().getResource("Textures/mob1.png");
		tileset_mob[0] = new ImageIcon(mobLoc).getImage();
		
		URL bossLoc = getClass().getResource("Textures/boss1.png");
		tileset_mob[1] = new ImageIcon(bossLoc).getImage();
		


		ClassLoader classLoader = getClass().getClassLoader();
		InputStream stream = classLoader.getResourceAsStream("Mission");
		
		save.loadSave(stream);
		
		for(int i =0; i< mobs.length;i++){
			mobs[i] = new Mob();
		}
		

		
		
	}

	public void paintComponent(Graphics g){
		if(isFirst){
			myWidth = getWidth();
			myHeight = getHeight();
			define();
			
					
			isFirst = false;
			
		}
		g.setColor(new Color(60,60,60));
		g.fillRect(0, 0, getWidth(), getHeight());
		g.setColor(new Color(0,0,0));
		g.drawLine(room.block[0][0].x-1, 0, room.block[0][0].x-1, room.block[room.worldHeight -1][0].y + room.blockSize);//Drawing Left Line
		g.drawLine(room.block[0][room.worldWidth -1].x + room.blockSize, 0, room.block[0][room.worldWidth -1].x + room.blockSize, room.block[room.worldHeight -1][0].y + room.blockSize);//Drawing Right Line
		g.drawLine(room.block[0][0].x, room.block[room.worldHeight-1][0].y + room.blockSize, room.block[0][room.worldWidth -1].x + room.blockSize,room.block[room.worldHeight -1][0].y + room.blockSize);//Drawing Bottom Line
		
		
		room.draw(g);// Draw the Room(MAP)
		
		for(int i =0; i< mobs.length;i++){
			if(mobs[i].inGame){
				mobs[i].draw(g);
			}
			
			
		store.draw(g);// Draw the Store
		
		if(health < 1){
			g.setColor(new Color(240,20,20));
			g.fillRect(0,0,myWidth,myHeight);
			g.setColor(new Color(255,255,255));
			g.setFont(new Font("Arial",Font.BOLD,14));
			g.drawString("Game Over!", 10, 20); //TODO Set Game over text to be in the middle of the Window!
		}
	
		}
	}


	public static int fpsFrame = 0, fps = 1000000;
	
	public int spawnTime = 2000, spawnFrame = 0;

	public void mobSpawner(){
		if(mobSpawned <= mobNumber){
			if(spawnFrame >= spawnTime){
				for(int i=0;i<mobs.length;i++){
					if(!mobs[i].inGame){
						if(mobSpawned == mobNumber){
							mobs[mobNumber].towerDamage = 5;// Bosses Damage to our health from getting home
							mobs[mobNumber].spawnMob(Value.mobBoss,1000 * level, 22* level, 10, (25 / Screen.level));
							System.out.println("Boss Spawned! "+ "Health: " + mobs[mobNumber].health + " Tower Damage: " + mobs[mobNumber].towerDamage + " Count: " + mobSpawned + " At Speed " + mobs[mobNumber].walkSpeed);

						}else{
							mobs[i].towerDamage = 15;// Normal creep Damage to our health from getting home
							mobs[i].spawnMob(Value.mobAngry, 1000 * level,22 * level, 5, (25 / Screen.level));
							System.out.println("Normal Spawned! "+ "Health: " + mobs[i].health + " Tower Damage: " + mobs[i].towerDamage + " Count: " + mobSpawned + " At Speed " + mobs[i].walkSpeed);	
						}

						mobSpawned +=1;
						break;
					}
				}

				spawnFrame = 0;
			}else{
				spawnFrame += 1;
			}
		}
	}
		
	
	public void run(){
		
		while(true){
			if(!isFirst && health > 0){
				room.physic();
				mobSpawner();	
				for(int i =0; i< mobs.length;i++){
					if(mobs[i].inGame){
					mobs[i].physic();
					}
				}
				if(killCount == mobNumber +1){
					level += 1;
					killCount =0;
					mobSpawned =0;
				}
			}
			

			repaint();
			
			
			try{
				Thread.sleep(1);
			}catch(Exception e){}	
		}
		
	}

}
