package twilightforest.entity.boss;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.boss.EntityDragonPart;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityTFNagaSegment extends Entity {
	private EntityTFNaga naga;
	private int segment;
	private int deathCounter;
	private final AttributeModifier slowdown = new AttributeModifier("Long Naga Slowdown", -0.2F / 12F, 0).setSaved(false);

	public EntityTFNagaSegment(World par1World) {
		super(par1World);
        setSize(1.8F, 1.8F);
		this.stepHeight = 2;
	}

	public EntityTFNagaSegment(EntityTFNaga myNaga, int segNum) {
		this(myNaga.getWorld());
		this.naga = myNaga;
		this.segment = segNum;
		myNaga.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).applyModifier(slowdown);
	}

	@Override
	public void setDead() {
		super.setDead();
		if (naga != null) {
			naga.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).removeModifier(slowdown);
		}
	}

    @Override
	public boolean attackEntityFrom(DamageSource damagesource, float damage) {
		if (damagesource.isExplosion() || damagesource.isFireDamage()) {
			return false;
		}

		return naga != null && naga.attackEntityFrom(damagesource, damage * 2.0F / 3.0F);
	}

    @Override
    public void onUpdate() {
    	super.onUpdate();
    	
    	if (this.naga == null || this.naga.isDead) {
    		this.setDead();
    	}

    	this.ticksExisted++;
    	
    	lastTickPosX = posX;
    	lastTickPosY = posY;
    	lastTickPosZ = posZ;

    	for (; rotationYaw - prevRotationYaw < -180F; prevRotationYaw -= 360F) { }
    	for (; rotationYaw - prevRotationYaw >= 180F; prevRotationYaw += 360F) { }
    	for (; rotationPitch - prevRotationPitch < -180F; prevRotationPitch -= 360F) { }
    	for (; rotationPitch - prevRotationPitch >= 180F; prevRotationPitch += 360F) { }

       	
    	if (!this.onGround)
    	{
    		this.motionY -= 0.08D;
    	}
    	else
    	{
            this.motionX *= 0.800000011920929D;
            this.motionZ *= 0.800000011920929D;
    	}
    	
        this.move(motionX, motionY, motionZ);
        
        collideWithOthers();
        
		if (deathCounter > 0) {
			deathCounter--;
			
			if (deathCounter == 0) {
				
                for(int k = 0; k < 20; k++)
                {
                    double d = rand.nextGaussian() * 0.02D;
                    double d1 = rand.nextGaussian() * 0.02D;
                    double d2 = rand.nextGaussian() * 0.02D;
                    EnumParticleTypes explosionType = rand.nextBoolean() ?  EnumParticleTypes.EXPLOSION_LARGE : EnumParticleTypes.EXPLOSION_NORMAL;
                    
                    world.spawnParticle(explosionType, (posX + rand.nextFloat() * width * 2.0F) - width, posY + rand.nextFloat() * height, (posZ + rand.nextFloat() * width * 2.0F) - width, d, d1, d2);
                }
                
                setDead();
			}
		}

    }
    
	private void collideWithOthers()
    {
        List<Entity> list = this.world.getEntitiesWithinAABBExcludingEntity(this, this.getEntityBoundingBox().expand(0.20000000298023224D, 0.0D, 0.20000000298023224D));

        for (Entity entity : list)
        {
        	if (entity.canBePushed())
        	{
        		this.collideWithEntity(entity);
        	}
        }
    }
    
    private void collideWithEntity(Entity entity) {
    	entity.applyEntityCollision(this);
    	
		// attack anything that's not us
		if ((entity instanceof EntityLivingBase) && !(entity instanceof EntityTFNaga) && !(entity instanceof EntityTFNagaSegment))
		{
			naga.attackTime = 10;
    		int attackStrength = 2;
    		
    		// get rid of nearby deer & look impressive
    		if (entity instanceof EntityAnimal)
    		{
    			attackStrength *= 3;
    		}
    		
			entity.attackEntityFrom(DamageSource.causeMobDamage(naga), attackStrength);
		}

		
	}

	@Override
    public void setRotation(float par1, float par2)
    {
        this.rotationYaw = MathHelper.wrapDegrees(par1 % 360.0F);
        this.rotationPitch = par2 % 360.0F;
    }

	@Override
    public boolean canBeCollidedWith()
    {
        return true;
    }

    @Override
    public boolean canBePushed()
    {
        return false;
    }
    
	@Override
    public boolean isEntityEqual(Entity entity)
    {
        return this == entity || this.naga == entity;
    }

	@Override
	protected void entityInit() { }

	@Override
	protected void readEntityFromNBT(NBTTagCompound nbttagcompound) { }

	@Override
	protected void writeEntityToNBT(NBTTagCompound nbttagcompound) { }

	@Override
    protected void playStepSound(BlockPos pos, Block par4) {}
    
	public void selfDestruct() {
		this.deathCounter = 30;
	}
}
