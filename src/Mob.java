import java.awt.*;

public class Mob extends Rectangle {
	public int health = 1000;
	public int healthSpace = 3, healthHeight = 6;
    
	public int healthBarSize = 19;//So that the health bar isnt too big
	public int xC, yC;
	public int mobSize = 52;
	public int mobWalk =0;
	public int upward = 0, downward = 1, right =2, left = 3;
	public int direction = right;	
	public int mobID = Value.mobAir;
	public boolean inGame = false;
	public boolean hasUpward = false;
	public boolean hasDown = false;
	public boolean hasLeft = false;
	public boolean hasRight = false;
	
	public boolean gettingShot = false;
	public int walkFrame = 0, walkSpeed = 30; //Walking Speed of mobs
	public boolean slowed = false;
	
	public  int damage;// Damage to our health from getting home
	public  int towerDamage = 1;
	
	public Mob(){
		
	}
	public void spawnMob(int mobID, int mobHealth, int barSize, int damage, int walkingSpeed){
		
			for(int y=0;y<Screen.room.block.length;y++){
				if(Screen.room.block[y][0].groundID == Value.groundRoad){
					setBounds(Screen.room.block[y][0].x,Screen.room.block[y][0].y,mobSize,mobSize);
					xC = 0;
					yC = y;
				
				}
			}
		
		this.mobID = mobID;	
		this.health = mobHealth;
		inGame = true;
		this.damage = damage;
		//this.healthBarSize = barSize;
		this.walkSpeed = walkingSpeed;
		slowed = false;
		
	}
	
	public void deleteMob(){
		inGame = false;
		direction = right;
		mobWalk =0;
		Screen.killCount +=1; 
	}
	
	public void losePlayerHealth(){
			Screen.health -= damage; 		
	}
	
	
	public void slow(){
			walkSpeed = ((25 / Screen.level)* 3)*2;
			slowed = true;
			health -= 2;
		
	}
	
	public void physic(){
	
		//Start Mob Path Finding
		if(walkFrame >= walkSpeed){
			if(direction == right){
				x += 1;
				}else if(direction == left){
					x -= 1;
				} else if(direction == upward){
					y -= 1;
				} else if(direction == downward){
					y += 1;
				}
				mobWalk += 1;
				
				
				if(mobWalk == Screen.room.blockSize){
					if(direction == right){
						xC += 1;
						hasRight = true;
						} else if(direction == upward){
							yC -= 1;
							hasUpward = true;
						} else if(direction == downward){
							yC += 1;
							hasDown = true;
						} else if(direction == left){
							xC -= 1;
							hasLeft = true;
						}
					if(!hasUpward){
					try{
						if(Screen.room.block[yC + 1][xC].groundID == Value.groundRoad){//Going down!
							direction = downward;
						}
					}catch(Exception e){}
					}
					if(!hasDown){	
						try{

							if(Screen.room.block[yC - 1][xC].groundID == Value.groundRoad){//Going Up!
								direction = upward;
							}			
						}catch(Exception e){}
					}
					if(!hasLeft){
						try{
							if(Screen.room.block[yC][xC + 1].groundID == Value.groundRoad){//Going Right!
								direction = right;
							}

						}catch(Exception e){}
					}
					if(!hasRight){
						try{
							if(Screen.room.block[yC][xC - 1].groundID == Value.groundRoad){//Going Left!
								direction = left;
							}

						}catch(Exception e){}
					}
					
					if(Screen.room.block[yC][xC].airID == Value.airHome){
						deleteMob();
						losePlayerHealth();
						
					}
					hasUpward = false;
					hasDown = false;
					hasLeft = false;
					hasRight = false;
					mobWalk = 0;
				}
				
				walkFrame = 0;
			
		}	
		else{
			walkFrame +=1;
		}
}
	//End Mob Path Finding
		
		public void loseHealth(){
			health -= towerDamage;
			
			checkDeath();
			
		}
		
	public void checkDeath(){
		if(health <= 0){
			Screen.coinage += Value.deathReward[mobID];	//Player gets money from killed mobs
			System.out.println("Value of Gold: " + Value.deathReward[mobID] + " for killing MobID: " + mobID);
			deleteMob();
			
		}
	}
	
	public boolean isDead(){
		if(inGame){
			return false;
		}else{
			return true;
		}
		
	}
	public void draw(Graphics g){
		if(inGame){
			
			g.drawImage(Screen.tileset_mob[mobID],x, y, width, height,null);
			
			//System.out.println(width);
			//health bar
			if(slowed){//Slowed Colour (Blue)
				
				//Health bar
				g.setColor(new Color(180,50,50));
				g.fillRect(x,y - (healthSpace + healthHeight), width, healthHeight);

				//Current health
				g.setColor(new Color(150, 255, 255));
				g.fillRect(x,y - (healthSpace + healthHeight), health/ healthBarSize, healthHeight);

				
				g.setColor(new Color(0,0,0));
				g.drawRect(x,y - (healthSpace + healthHeight), (health/ healthBarSize) - 1, healthHeight-1);
			}else{//Normal 
				
				//Health bar
				g.setColor(new Color(180,50,50));
				g.fillRect(x,y - (healthSpace + healthHeight), width, healthHeight);
				
				//Current health
				g.setColor(new Color(50,180,50));
				g.fillRect(x,y - (healthSpace + healthHeight), health/ healthBarSize, healthHeight);
				
				g.setColor(new Color(0,0,0));
				g.drawRect(x,y - (healthSpace + healthHeight), (health/ healthBarSize) - 1, healthHeight-1);
			}
		}
		
	}
	
	
}
