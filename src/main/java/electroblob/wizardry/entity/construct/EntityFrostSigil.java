package electroblob.wizardry.entity.construct;

import java.util.List;

import electroblob.wizardry.Wizardry;
import electroblob.wizardry.registry.WizardryPotions;
import electroblob.wizardry.registry.WizardrySounds;
import electroblob.wizardry.util.MagicDamage;
import electroblob.wizardry.util.MagicDamage.DamageType;
import electroblob.wizardry.util.WizardryParticleType;
import electroblob.wizardry.util.WizardryUtilities;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public class EntityFrostSigil extends EntityMagicConstruct {

	public EntityFrostSigil(World par1World){
		super(par1World);
		this.height = 0.2f;
		this.width = 2.0f;
	}

	public EntityFrostSigil(World par1World, double x, double y, double z, EntityLivingBase caster,
			float damageMultiplier){
		super(par1World, x, y, z, caster, -1, damageMultiplier);
		this.height = 0.2f;
		this.width = 2.0f;
	}

	// Overrides the original to stop the entity moving when it intersects stuff. The default arrow does this to allow
	// it to stick in blocks.
	public void setPositionAndRotation2(double par1, double par3, double par5, float par7, float par8, int par9){
		this.setPosition(par1, par3, par5);
		this.setRotation(par7, par8);
	}

	public void onUpdate(){

		super.onUpdate();

		if(!this.world.isRemote){

			List<EntityLivingBase> targets = WizardryUtilities.getEntitiesWithinRadius(1.0d, this.posX, this.posY,
					this.posZ, this.world);

			for(EntityLivingBase target : targets){

				if(this.isValidTarget(target)){

					double velX = target.motionX;
					double velY = target.motionY;
					double velZ = target.motionZ;

					target.attackEntityFrom(this.getCaster() != null
							? MagicDamage.causeIndirectMagicDamage(this, this.getCaster(), DamageType.FROST)
							: DamageSource.MAGIC, 8);

					// Removes knockback
					target.motionX = velX;
					target.motionY = velY;
					target.motionZ = velZ;

					if(!MagicDamage.isEntityImmune(DamageType.FROST, target))
						target.addPotionEffect(new PotionEffect(WizardryPotions.frost, 200, 1));

					this.playSound(WizardrySounds.SPELL_FREEZE, 1.0f, 1.0f);

					// The trap is destroyed once triggered.
					this.setDead();
				}
			}
		}else if(this.rand.nextInt(15) == 0){
			double radius = 0.5 + rand.nextDouble() * 0.3;
			double angle = rand.nextDouble() * Math.PI * 2;
			Wizardry.proxy.spawnParticle(WizardryParticleType.SNOW, world, this.posX + radius * Math.cos(angle),
					this.posY + 0.1, this.posZ + radius * Math.sin(angle), 0, 0, 0, 40 + rand.nextInt(10));
		}
	}

	@Override
	protected void entityInit(){

	}

	/**
	 * Return whether this entity should be rendered as on fire.
	 */
	public boolean canRenderOnFire(){
		return false;
	}

}
