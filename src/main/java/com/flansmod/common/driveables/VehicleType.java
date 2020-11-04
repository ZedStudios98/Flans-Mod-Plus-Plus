package com.flansmod.common.driveables;

import java.util.ArrayList;

import com.flansmod.client.model.ModelVehicle;
import com.flansmod.common.FlansMod;
import com.flansmod.common.driveables.DriveableType.ParticleEmitter;
import com.flansmod.common.types.TypeFile;
import com.flansmod.common.vector.Vector3f;
import com.flansmod.common.vector.Vector3i;

public class VehicleType extends DriveableType
{
	/** Movement modifiers */
	public float turnLeftModifier = 1F, turnRightModifier = 1F;
	/** If true, this will crush any living entity under the wheels */
	public boolean squashMobs = false;
	/** If this is true, the vehicle will drive from all wheels */
	public boolean fourWheelDrive = false;
	/** If true, then wheels will rotate as the vehicle drives */
	public boolean rotateWheels = false;
	/** Tank movement system. Uses track collision box for thrust, rather than the wheels */
	public boolean tank = false;
	/** The traction modifier of the vehicle.  Higher tractions means less drifting when steering */
	public float traction = 15F;
	/** The throttle level at which vehicles begin to loose traction and start drifting when turning */
	public float driftSpeed = 0.7F;
	/** The multiplier of how much traction is lost when the throttle is above driftSpeed */
	public float driftTraction = 1.0F;
	
	/** How fast the vehicle throttles up outside Cruise Mode */
	public float acceleration = 1F;
	/** How fast the vehicle throttles down outside Cruise Mode */
	public float deceleration = 1F;

	/** Shoot delays */
	public int vehicleShootDelay, vehicleShellDelay;
	/** Aesthetic door variable */
    public boolean hasDoor = false;
    
    
	//Door animations
	public Vector3f doorPos1 = new Vector3f(0,0,0);
	public Vector3f doorPos2 = new Vector3f(0,0,0);
	public Vector3f doorRot1 = new Vector3f(0,0,0);
	public Vector3f doorRot2 = new Vector3f(0,0,0);
	public Vector3f doorRate = new Vector3f(0,0,0);
	public Vector3f doorRotRate = new Vector3f(0,0,0);
	
	public ArrayList<SmokePoint> smokers = new ArrayList<SmokePoint>();

	public static ArrayList<VehicleType> types = new ArrayList<VehicleType>();

    public VehicleType(TypeFile file)
    {
		super(file);
		types.add(this);
    }

    @Override
	public void preRead(TypeFile file)
    {
    	super.preRead(file);
    	wheelPositions = new DriveablePosition[4];
    }

    @Override
	protected void postRead(TypeFile file)
    {
    	super.postRead(file);
    }

    @Override
	protected void read(String[] split, TypeFile file)
	{
		super.read(split, file);
		try
		{
			//Movement modifiers
			if(split[0].equals("TurnLeftSpeed"))
				turnLeftModifier = Float.parseFloat(split[1]);
			if(split[0].equals("TurnRightSpeed"))
				turnRightModifier = Float.parseFloat(split[1]);
			if(split[0].equals("SquashMobs"))
				squashMobs = Boolean.parseBoolean(split[1].toLowerCase());
            if(split[0].equals("FourWheelDrive"))
            	fourWheelDrive = Boolean.parseBoolean(split[1].toLowerCase());
            if(split[0].equals("Tank") || split[0].equals("TankMode"))
            	tank = Boolean.parseBoolean(split[1].toLowerCase());
			if(split[0].equals("Traction"))
				traction = Float.parseFloat(split[1]);
			if(split[0].equals("DriftSpeedThreshold"))
				driftSpeed = Float.parseFloat(split[1]);
			if(split[0].equals("DriftTractionLoss"))
				driftTraction = Float.parseFloat(split[1]);

            //Visuals
            if(split[0].equals("HasDoor"))
                hasDoor = Boolean.parseBoolean(split[1].toLowerCase());
            if(split[0].equals("RotateWheels"))
            	rotateWheels = Boolean.parseBoolean(split[1].toLowerCase());
            
            //Animations
            if(split[0].equals("DoorPosition1"))
            	doorPos1 = new Vector3f(split[1], shortName);
            if(split[0].equals("DoorPosition2"))
            	doorPos2 = new Vector3f(split[1], shortName);
            if(split[0].equals("DoorRotation1"))
            	doorRot1 = new Vector3f(split[1], shortName);
            if(split[0].equals("DoorRotation2"))
            	doorRot2 = new Vector3f(split[1], shortName);
            if(split[0].equals("DoorRate"))
            	doorRate = new Vector3f(split[1], shortName);
            if(split[0].equals("DoorRotRate"))
            	doorRotRate = new Vector3f(split[1], shortName);

			//Armaments
			if(split[0].equals("ShootDelay"))
				vehicleShootDelay = Integer.parseInt(split[1]);
			if(split[0].equals("ShellDelay"))
				vehicleShellDelay = Integer.parseInt(split[1]);

			//Sound
			if(split[0].equals("ShootSound"))
			{
				shootSoundPrimary = split[1];
				FlansMod.proxy.loadSound(contentPack, "driveables", split[1]);
			}
			if(split[0].equals("ShellSound"))
			{
				shootSoundSecondary = split[1];
				FlansMod.proxy.loadSound(contentPack, "driveables", split[1]);
			}
			
			if(split[0].equalsIgnoreCase("AddSmokePoint") || split[0].equalsIgnoreCase("AddSmokeDispenser"))
			{
				SmokePoint smoke = new SmokePoint();
				smoke.position = new Vector3f(split[1], shortName);
				smoke.direction = new Vector3f(split[2], shortName);
				smoke.detTime = Integer.parseInt(split[3]);
				smoke.part = split[4];
				smokers.add(smoke);
			}
		}
		catch (Exception ignored)
		{
		}
		
		if (traction < 1F)
			traction = 1F;
		//if (traction > 100F)
			//traction = 100F;
	}

	public static VehicleType getVehicle(String find)
	{
		for(VehicleType type : types)
		{
			if(type.shortName.equals(find))
				return type;
		}
		return null;
	}
	
	public class SmokePoint
	{
		public Vector3f position;
		public Vector3f direction;
		public int detTime;
		public String part;
	}

	/** To be overriden by subtypes for model reloading */
	public void reloadModel()
	{
		model = FlansMod.proxy.loadModel(modelString, shortName, ModelVehicle.class);
	}
}