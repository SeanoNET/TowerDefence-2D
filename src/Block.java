import java.awt.*;

public class Block extends Rectangle{
	public Rectangle towerSquare;
	public int towerSquareSize =130;
	public int groundID;
	public int airID;
	
	public int loseTime = 100 ,loseFrame = 0;
	public int shotMob = -1;
	public boolean isShooting = false;
	public int towerDamage = 1;

	public Block(int x, int y, int width, int height, int groundID,int airID) {
		setBounds(x , y, width, height);
		towerSquare = new Rectangle(x - (towerSquareSize/2), y- (towerSquareSize/2), width + (towerSquareSize), height + (towerSquareSize));
		this.groundID = groundID;
		this.airID = airID;
		
	}

	public void draw(Graphics g){
		//g.drawRect(x, y, width, height);
		g.drawImage(Screen.tileset_groundGrass[groundID],x, y, width, height,null);
		
		if(airID != Value.airAir){
			
			g.drawImage(Screen.tileset_air[airID],x, y, width, height,null);
			
		}
	}
	
	public void physic(){
		if(shotMob != -1 && towerSquare.intersects(Screen.mobs[shotMob])){
			isShooting = true;
		}
		else{			
			isShooting = false;
		}
		if(!isShooting){
			if (airID == Value.airTowerLase || airID ==  Value.airTowerFrost){
				for(int i=0;i<Screen.mobs.length;i++){
					if(Screen.mobs[i].inGame){
						if(towerSquare.intersects(Screen.mobs[i])){
							isShooting = true;
							shotMob = i;
						}
					}
				}
			}
		}
		if(isShooting){
			if(loseFrame >= loseTime){
				if(airID ==  Value.airTowerFrost){
					Screen.mobs[shotMob].slow();
					Screen.mobs[shotMob].gettingShot = true;
					
				}else{
					Screen.mobs[shotMob].loseHealth();
				}
				
				loseFrame = 0;
			}else{
				loseFrame += 1;
			}	
			
			if(Screen.mobs[shotMob].isDead() && !Screen.mobs[shotMob].inGame){
				isShooting = false;
				shotMob = -1;
			}
		}
	}

	
	public void fight(Graphics g){
		if(Screen.store.holdsItem){
			if(airID == Value.airTowerLase || airID == Value.airTowerFrost){

				g.drawRect(towerSquare.x, towerSquare.y, towerSquare.width, towerSquare.height);
				
			}
		}
		if(isShooting){
			if(airID == Value.airTowerLase){
			g.setColor(new Color(240,20,20));
			g.drawLine(x + (width/2), y + (height/2), Screen.mobs[shotMob].x + (Screen.mobs[shotMob].width/2), Screen.mobs[shotMob].y + (Screen.mobs[shotMob].height/2));
			}else if(airID == Value.airTowerFrost){
				g.setColor(new Color(150, 255, 255));
				g.drawLine(x + (width/2), y + (height/2), Screen.mobs[shotMob].x + (Screen.mobs[shotMob].width/2), Screen.mobs[shotMob].y + (Screen.mobs[shotMob].height/2));
			}
		}
		
	}
}
