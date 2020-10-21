package com.flansmod.client.model;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.IItemRenderer.ItemRenderType;
import net.minecraftforge.client.IItemRenderer.ItemRendererHelper;

import com.flansmod.client.FlansModResourceHandler;
import com.flansmod.client.tmt.ModelRendererTurbo;
import com.flansmod.common.FlansMod;
import com.flansmod.common.driveables.DriveablePart;
import com.flansmod.common.driveables.DriveablePosition;
import com.flansmod.common.driveables.EntityVehicle;
import com.flansmod.common.driveables.EnumDriveablePart;
import com.flansmod.common.driveables.ItemVehicle;
import com.flansmod.common.driveables.ShootPoint;
import com.flansmod.common.driveables.VehicleType;

public class RenderVehicle extends Render implements IItemRenderer
{
    public RenderVehicle()
    {
        shadowSize = 1.0F;
    }

    public void render(EntityVehicle vehicle, double d, double d1, double d2, float f, float f1)
    {
    	if(vehicle.ridingEntity != null)
    	{
    		if(vehicle.ridingEntity.getClass().toString().indexOf("mcheli.aircraft.MCH_EntitySeat") > 0)
    		{
    			return;
    		}
    	}
			
		GL11.glAlphaFunc(GL11.GL_GREATER, 0.001F);
		GL11.glEnable(GL11.GL_BLEND);
		int srcBlend = GL11.glGetInteger(GL11.GL_BLEND_SRC);
		int dstBlend = GL11.glGetInteger(GL11.GL_BLEND_DST);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

    	bindEntityTexture(vehicle);
    	VehicleType type = vehicle.getVehicleType();
        GL11.glPushMatrix();
        {
	        GL11.glTranslatef((float)d, (float)d1, (float)d2);
	        float dYaw = (vehicle.axes.getYaw() - vehicle.prevRotationYaw);
	        for(; dYaw > 180F; dYaw -= 360F) {}
	        for(; dYaw <= -180F; dYaw += 360F) {}
	        float dPitch = (vehicle.axes.getPitch() - vehicle.prevRotationPitch);
	        for(; dPitch > 180F; dPitch -= 360F) {}
	        for(; dPitch <= -180F; dPitch += 360F) {}
	        float dRoll = (vehicle.axes.getRoll() - vehicle.prevRotationRoll);
	        for(; dRoll > 180F; dRoll -= 360F) {}
	        for(; dRoll <= -180F; dRoll += 360F) {}
	        GL11.glRotatef(180F - vehicle.prevRotationYaw - dYaw * f1, 0.0F, 1.0F, 0.0F);
	        GL11.glRotatef(vehicle.prevRotationPitch + dPitch * f1, 0.0F, 0.0F, 1.0F);
			GL11.glRotatef(vehicle.prevRotationRoll + dRoll * f1, 1.0F, 0.0F, 0.0F);
			GL11.glRotatef(180F, 0.0F, 1.0F, 0.0F);
			GL11.glPushMatrix();
			{
				ModelVehicle modVehicle = (ModelVehicle)type.model;
				
				GL11.glPushMatrix();
				{
					float recoilDPos = (float)Math.sin(Math.toRadians(vehicle.recoilPos)) - (float)Math.sin(Math.toRadians(vehicle.lastRecoilPos));
					float recoilPos = (float)Math.sin(Math.toRadians(vehicle.lastRecoilPos)) + recoilDPos*f1;

					GL11.glScalef(type.modelScale, type.modelScale, type.modelScale);
					if(modVehicle != null)
						modVehicle.render(vehicle, f1);
					
					if(type.turretOrigin != null && vehicle.isPartIntact(EnumDriveablePart.turret) && vehicle.seats != null && vehicle.seats[0] != null)
					{
						dYaw = (vehicle.seats[0].looking.getYaw() - vehicle.seats[0].prevLooking.getYaw());
						float pitch = vehicle.seats[0].looking.getPitch();
				        for(; dYaw > 180F; dYaw -= 360F) {}
				        for(; dYaw <= -180F; dYaw += 360F) {}
			    		float yaw = vehicle.seats[0].prevLooking.getYaw() + dYaw * f1;
			    		
			    		//rotate and render turret
			    		GL11.glTranslatef(type.turretOrigin.x, type.turretOrigin.y, type.turretOrigin.z);
						GL11.glRotatef(-yaw, 0.0F, 1.0F, 0.0F);
						GL11.glTranslatef(-type.turretOrigin.x, -type.turretOrigin.y, -type.turretOrigin.z);
						
						if(modVehicle != null)
							modVehicle.renderTurret(0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F, vehicle, f1);
						
						//rotate and render barrel
						if(modVehicle != null){
						GL11.glTranslatef(modVehicle.barrelAttach.x,modVehicle.barrelAttach.y, -modVehicle.barrelAttach.z);
						float bPitch = (vehicle.seats[0].looking.getPitch() - vehicle.seats[0].prevLooking.getPitch());
			    		float aPitch = vehicle.seats[0].prevLooking.getPitch() + bPitch * f1;

						GL11.glRotatef(-aPitch, 0F, 0F, 1F);
						GL11.glTranslatef(recoilPos*-(5F/16F),0F, 0F);
						modVehicle.renderAnimBarrel(0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F, vehicle, f1);
						}

						
					}
				}
				GL11.glPopMatrix();

				GL11.glPushMatrix();
				if(FlansMod.DEBUG)
				{				
					if(type.turretOrigin != null && vehicle.isPartIntact(EnumDriveablePart.turret) && vehicle.seats != null && vehicle.seats[0] != null)
					{
						dYaw = (vehicle.seats[0].looking.getYaw() - vehicle.seats[0].prevLooking.getYaw());
				        for(; dYaw > 180F; dYaw -= 360F) {}
				        for(; dYaw <= -180F; dYaw += 360F) {}
			    		float yaw = vehicle.seats[0].prevLooking.getYaw() + dYaw * f1;
			    		
			    		GL11.glTranslatef(type.turretOrigin.x, type.turretOrigin.y, type.turretOrigin.z);
						GL11.glRotatef(-yaw, 0.0F, 1.0F, 0.0F);
//						GL11.glTranslatef(-type.turretOrigin.x, -type.turretOrigin.y, -type.turretOrigin.z);
//			    		GL11.glTranslatef(type.turretOrigin.x, type.turretOrigin.y, type.turretOrigin.z);
						GL11.glRotatef(-vehicle.seats[0].looking.getPitch(), 0.0F, 0.0F, 1.0F);
						GL11.glTranslatef(-type.turretOrigin.x, -type.turretOrigin.y, -type.turretOrigin.z);
						
						//Render shoot points
						GL11.glColor4f(0F, 0F, 1F, 0.3F);
						for(ShootPoint point : type.shootPointsPrimary)			
							if(point.rootPos.part == EnumDriveablePart.turret)
								renderAABB(AxisAlignedBB.getBoundingBox(point.rootPos.position.x - 0.25F, point.rootPos.position.y - 0.25F, point.rootPos.position.z - 0.25F, point.rootPos.position.x + 0.25F, point.rootPos.position.y + 0.25F, point.rootPos.position.z + 0.25F));
						
						GL11.glColor4f(0F, 1F, 0F, 0.3F);
						for(ShootPoint point : type.shootPointsSecondary)	
							if(point.rootPos.part == EnumDriveablePart.turret)
								renderAABB(AxisAlignedBB.getBoundingBox(point.rootPos.position.x - 0.25F, point.rootPos.position.y - 0.25F, point.rootPos.position.z - 0.25F, point.rootPos.position.x + 0.25F, point.rootPos.position.y + 0.25F, point.rootPos.position.z + 0.25F));
					}
				}
				GL11.glPopMatrix();
				if(modVehicle != null)
				{
					GL11.glPushMatrix();
					
					GL11.glTranslatef(modVehicle.drillHeadOrigin.x, modVehicle.drillHeadOrigin.y, modVehicle.drillHeadOrigin.z);
					GL11.glRotatef(vehicle.harvesterAngle * 50F, 1.0F, 0.0F, 0.0F);
					GL11.glTranslatef(-modVehicle.drillHeadOrigin.x, -modVehicle.drillHeadOrigin.y, -modVehicle.drillHeadOrigin.z);
					modVehicle.renderDrillBit(vehicle, f1);
					
					GL11.glPopMatrix();
				}
				
				if(modVehicle != null)
				{
					//Rotate/Render door
					GL11.glPushMatrix();
					GL11.glTranslatef(modVehicle.doorAttach.x + vehicle.doorPos.x/16, modVehicle.doorAttach.y + vehicle.doorPos.y/16, -modVehicle.doorAttach.z + vehicle.doorPos.z/16);
					GL11.glRotatef(vehicle.doorRot.x, 1F, 0F, 0F);
					GL11.glRotatef(-vehicle.doorRot.y, 0F, 1F, 0F);
					GL11.glRotatef(vehicle.doorRot.z, 0F, 0F, 1F);
					modVehicle.renderDoor(vehicle, 0.0625F);
					GL11.glPopMatrix();
				}
			}
			GL11.glPopMatrix();
			
			if(FlansMod.DEBUG)
			{
				GL11.glDisable(GL11.GL_TEXTURE_2D);
				GL11.glEnable(GL11.GL_BLEND);
				GL11.glDisable(GL11.GL_DEPTH_TEST);
				GL11.glColor4f(1F, 0F, 0F, 0.3F);
				GL11.glScalef(1F, 1F, 1F);
				for(DriveablePart part : vehicle.getDriveableData().parts.values())
				{
					if(part.box == null)
						continue;
					
					renderAABB(AxisAlignedBB.getBoundingBox(part.box.x, part.box.y, part.box.z, (part.box.x + part.box.w), (part.box.y + part.box.h), (part.box.z + part.box.d)));
				}
				//GL11.glColor4f(0F, 1F, 0F, 0.3F);
				//if(type.barrelPosition != null)
				//	renderAABB(AxisAlignedBB.getBoundingBox(type.barrelPosition.x - 0.25F, type.barrelPosition.y - 0.25F, type.barrelPosition.z - 0.25F, type.barrelPosition.x + 0.25F, type.barrelPosition.y + 0.25F, type.barrelPosition.z + 0.25F));
				
				//Render shoot points
				GL11.glColor4f(0F, 0F, 1F, 0.3F);
				for(ShootPoint point : type.shootPointsPrimary)
					if(point.rootPos.part != EnumDriveablePart.turret)
						renderAABB(AxisAlignedBB.getBoundingBox(point.rootPos.position.x - 0.25F, point.rootPos.position.y - 0.25F, point.rootPos.position.z - 0.25F, point.rootPos.position.x + 0.25F, point.rootPos.position.y + 0.25F, point.rootPos.position.z + 0.25F));
				
				GL11.glColor4f(0F, 1F, 0F, 0.3F);
				for(ShootPoint point : type.shootPointsSecondary)	
					if(point.rootPos.part != EnumDriveablePart.turret)
						renderAABB(AxisAlignedBB.getBoundingBox(point.rootPos.position.x - 0.25F, point.rootPos.position.y - 0.25F, point.rootPos.position.z - 0.25F, point.rootPos.position.x + 0.25F, point.rootPos.position.y + 0.25F, point.rootPos.position.z + 0.25F));

				
				GL11.glEnable(GL11.GL_TEXTURE_2D);
				GL11.glEnable(GL11.GL_DEPTH_TEST);
				GL11.glDisable(GL11.GL_BLEND);
				GL11.glColor4f(1F, 1F, 1F, 1F);
			}
        }
        GL11.glPopMatrix();

		GL11.glBlendFunc(srcBlend, dstBlend);
		GL11.glDisable(GL11.GL_BLEND);
    }

    @Override
	public void doRender(Entity entity, double d, double d1, double d2, float f, float f1)
    {
        render((EntityVehicle)entity, d, d1, d2, f, f1);
    }
    
	@Override
	protected ResourceLocation getEntityTexture(Entity entity) 
	{
		return FlansModResourceHandler.getTexture(((EntityVehicle)entity).getVehicleType());
	}
	
	@Override
	public boolean handleRenderType(ItemStack item, ItemRenderType type) 
	{
		switch(type)
		{
		case EQUIPPED : case EQUIPPED_FIRST_PERSON : case ENTITY : return Minecraft.getMinecraft().gameSettings.fancyGraphics && item != null && item.getItem() instanceof ItemVehicle && ((ItemVehicle)item.getItem()).type.model != null;
		default : break;
		}
		return false;
	}

	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) 
	{
		return false;
	}

	@Override
	public void renderItem(ItemRenderType type, ItemStack item, Object... data) 
	{
		GL11.glAlphaFunc(GL11.GL_GREATER, 0.001F);
		GL11.glEnable(GL11.GL_BLEND);
		int srcBlend = GL11.glGetInteger(GL11.GL_BLEND_SRC);
		int dstBlend = GL11.glGetInteger(GL11.GL_BLEND_DST);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		GL11.glPushMatrix();
		if(item != null && item.getItem() instanceof ItemVehicle)
		{
			VehicleType vehicleType = ((ItemVehicle)item.getItem()).type;
			if(vehicleType.model != null)
			{
				float scale = 0.5F;
				switch(type)
				{
				case ENTITY:
				{
					scale = 2.5F;
					GL11.glRotatef(((EntityItem)data[1]).ticksExisted * 2, 0F, 1F, 0F);
					break;
				}
				case EQUIPPED:
				{
					GL11.glRotatef(15F, 0F, 0F, 1F);
					GL11.glRotatef(15F, 1F, 0F, 0F);
					GL11.glRotatef(270F, 0F, 1F, 0F);
					GL11.glTranslatef(0F, 0.15F, -0.4F);
					scale = 1.5F;
					break;
				}
				case EQUIPPED_FIRST_PERSON:
				{
					GL11.glRotatef(25F, 0F, 0F, 1F); 
					GL11.glRotatef(-5F, 0F, 1F, 0F);
					GL11.glTranslatef(0.15F, 0.4F, -0.6F);
					scale = 0.57F;
					break;
				}
				default : break;
				}
				
				GL11.glScalef(scale / vehicleType.cameraDistance, scale / vehicleType.cameraDistance, scale / vehicleType.cameraDistance);
				Minecraft.getMinecraft().renderEngine.bindTexture(FlansModResourceHandler.getTexture(vehicleType));
				ModelDriveable model = vehicleType.model;
				model.render(vehicleType);
			}
		}
		GL11.glPopMatrix();

		GL11.glBlendFunc(srcBlend, dstBlend);
		GL11.glDisable(GL11.GL_BLEND);
	}
}

