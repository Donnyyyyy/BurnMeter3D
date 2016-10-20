package ru.donny.burnmeter3D.controllers.bullettest;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.physics.bullet.collision.ClosestRayResultCallback;
import com.badlogic.gdx.physics.bullet.collision.Collision;
import com.badlogic.gdx.physics.bullet.collision.btCapsuleShape;
import com.badlogic.gdx.physics.bullet.dynamics.btConeTwistConstraint;
import com.badlogic.gdx.physics.bullet.dynamics.btConstraintSetting;
import com.badlogic.gdx.physics.bullet.dynamics.btDynamicsWorld;
import com.badlogic.gdx.physics.bullet.dynamics.btHingeConstraint;
import com.badlogic.gdx.physics.bullet.dynamics.btPoint2PointConstraint;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.physics.bullet.dynamics.btTypedConstraint;
import com.badlogic.gdx.utils.Array;

import ru.donny.burnmeter3D.engine.bullet.BulletConstructor;

public class RayPickRagdollTest extends BaseBulletTest {

	ArrayList<btRigidBody> rigids = new ArrayList<btRigidBody>();

	final Array<btTypedConstraint> constraints = new Array<btTypedConstraint>();
	btPoint2PointConstraint pickConstraint = null;
	btPoint2PointConstraint fixConstraint = null;
	btRigidBody pickedBody = null;
	float pickDistance;
	Vector3 tmpV = new Vector3();

	@Override
	public void create() {
		super.create();
		instructions = "Tap to shoot\nDrag ragdoll to pick\nLong press to toggle debug mode\nSwipe for next test\nCtrl+drag to rotate\nScroll to zoom";

		camera.position.set(4f, 2f, 4f);
		camera.lookAt(0f, 1f, 0f);
		camera.update();

		world.addConstructor("pelvis",
				new BulletConstructor(createCapsuleModel(0.15f, 0.2f), 1f, new btCapsuleShape(0.15f, 0.2f)));
		world.addConstructor("spine",
				new BulletConstructor(createCapsuleModel(0.15f, 0.28f), 1f, new btCapsuleShape(0.15f, 0.28f)));
		world.addConstructor("head",
				new BulletConstructor(createCapsuleModel(0.1f, 0.05f), 1f, new btCapsuleShape(0.1f, 0.05f)));
		world.addConstructor("upperleg",
				new BulletConstructor(createCapsuleModel(0.07f, 0.45f), 1f, new btCapsuleShape(0.07f, 0.45f)));
		world.addConstructor("lowerleg",
				new BulletConstructor(createCapsuleModel(0.05f, 0.37f), 1f, new btCapsuleShape(0.05f, 0.37f)));
		world.addConstructor("upperarm",
				new BulletConstructor(createCapsuleModel(0.05f, 0.33f), 1f, new btCapsuleShape(0.05f, 0.33f)));
		world.addConstructor("lowerarm",
				new BulletConstructor(createCapsuleModel(0.04f, 0.25f), 1f, new btCapsuleShape(0.04f, 0.25f)));

		world.add("ground", 0f, 0f, 0f).setColor(0.25f + 0.5f * (float) Math.random(),
				0.25f + 0.5f * (float) Math.random(), 0.25f + 0.5f * (float) Math.random(), 1f);

		addRagdoll(0, 1f, 0);

		Gdx.input.setInputProcessor(new InputMultiplexer(this, new CameraInputController(camera)));
	}

	@Override
	public void dispose() {
		for (int i = 0; i < constraints.size; i++) {
			((btDynamicsWorld) world.collisionWorld).removeConstraint(constraints.get(i));
			constraints.get(i).dispose();
		}
		constraints.clear();
		super.dispose();
	}

	@Override
	public void render() {
//		for (btRigidBody i : rigids) {
//			i.applyCentralImpulse(Vector3.Zero);
//			i.setGravity(Vector3.Zero);
//		}

		super.render();

		// fixConstraint.setPivotB(fixConstraint.getPivotInB().add(0, -0.1f,
		// 0));
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		boolean result = false;
		if (button == Buttons.LEFT) {
			Ray ray = camera.getPickRay(screenX, screenY);
			tmpV1.set(ray.direction).scl(5f).add(ray.origin);
			ClosestRayResultCallback cb = new ClosestRayResultCallback(ray.origin, tmpV1);
			world.collisionWorld.rayTest(ray.origin, tmpV1, cb);
			if (cb.hasHit()) {
				btRigidBody body = (btRigidBody) (cb.getCollisionObject());

				// body.setCollisionFlags(body.getCollisionFlags() &
				// ~CollisionFlags.CF_KINEMATIC_OBJECT);

				if (body != null && !body.isStaticObject() && !body.isKinematicObject()) {
					pickedBody = body;
					body.setActivationState(Collision.DISABLE_DEACTIVATION);

					cb.getHitPointWorld(tmpV);
					tmpV.mul(body.getCenterOfMassTransform().inv());

					pickConstraint = new btPoint2PointConstraint(body, tmpV);
					btConstraintSetting setting = pickConstraint.getSetting();
					setting.setImpulseClamp(1f);
					setting.setTau(0.0001f);
					pickConstraint.setSetting(setting);

					((btDynamicsWorld) world.collisionWorld).addConstraint(pickConstraint);

					pickDistance = tmpV1.sub(camera.position).len();
					result = true;
				}
			}
			cb.dispose();
		}
		return result ? result : super.touchDown(screenX, screenY, pointer, button);
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		boolean result = false;
		if (button == Buttons.LEFT) {
			if (pickConstraint != null) {
				((btDynamicsWorld) world.collisionWorld).removeConstraint(pickConstraint);
				((btDynamicsWorld) world.collisionWorld).setGravity(Vector3.Zero);

				// pickedBody.setCollisionFlags(pickedBody.getCollisionFlags() |
				// CollisionFlags.CF_KINEMATIC_OBJECT);

				pickConstraint.dispose();
				pickConstraint = null;
				result = true;
			}
			if (pickedBody != null) {
				pickedBody.forceActivationState(Collision.ACTIVE_TAG);
				pickedBody.setDeactivationTime(0f);
				pickedBody = null;
			}
		}
		return result ? result : super.touchUp(screenX, screenY, pointer, button);
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		boolean result = false;
		if (pickConstraint != null) {
			Ray ray = camera.getPickRay(screenX, screenY);
			tmpV1.set(ray.direction).scl(pickDistance).add(camera.position);
			pickConstraint.setPivotB(tmpV1);
			result = true;
		}
		return result ? result : super.touchDragged(screenX, screenY, pointer);
	}

	@Override
	public boolean tap(float x, float y, int count, int button) {
		shoot(x, y);
		return true;
	}

	final static float PI = MathUtils.PI;
	final static float PI2 = 0.5f * PI;
	final static float PI4 = 0.25f * PI;

	public void addRagdoll(final float x, final float y, final float z) {
		final Matrix4 tmpM = new Matrix4();
		btRigidBody pelvis = (btRigidBody) world.add("pelvis", x, y + 1, z).body;
		btRigidBody spine = (btRigidBody) world.add("spine", x, y + 1.2f, z).body;
		btRigidBody head = (btRigidBody) world.add("head", x, y + 1.6f, z).body;
		btRigidBody leftupperleg = (btRigidBody) world.add("upperleg", x - 0.18f, y + 0.65f, z).body;
		btRigidBody leftlowerleg = (btRigidBody) world.add("lowerleg", x - 0.18f, y + 0.2f, z).body;
		btRigidBody rightupperleg = (btRigidBody) world.add("upperleg", x + 0.18f, y + 0.65f, z).body;
		btRigidBody rightlowerleg = (btRigidBody) world.add("lowerleg", x + 0.18f, y + 0.2f, z).body;
		btRigidBody leftupperarm = (btRigidBody) world.add("upperarm",
				tmpM.setFromEulerAngles(PI2, 0, 0).trn(x - 0.35f, y + 1.45f, z)).body;
		btRigidBody leftlowerarm = (btRigidBody) world.add("lowerarm",
				tmpM.setFromEulerAngles(PI2, 0, 0).trn(x - 0.7f, y + 1.45f, z)).body;
		btRigidBody rightupperarm = (btRigidBody) world.add("upperarm",
				tmpM.setFromEulerAngles(-PI2, 0, 0).trn(x + 0.35f, y + 1.45f, z)).body;
		btRigidBody rightlowerarm = (btRigidBody) world.add("lowerarm",
				tmpM.setFromEulerAngles(-PI2, 0, 0).trn(x + 0.7f, y + 1.45f, z)).body;

//		rigids.add(pelvis);
//		rigids.add(spine);
//		rigids.add(head);
//		rigids.add(leftupperleg);
//		rigids.add(leftlowerleg);
//		rigids.add(rightupperleg);
//		rigids.add(rightlowerleg);
//		rigids.add(leftupperarm);
//		rigids.add(leftlowerarm);
//		rigids.add(rightupperarm);
//		rigids.add(rightlowerarm);

		// for (btRigidBody i : rigids) {
		// i.setCollisionFlags(i.getCollisionFlags() |
		// CollisionFlags.CF_KINEMATIC_OBJECT);
		// }

		final Matrix4 localA = new Matrix4();
		final Matrix4 localB = new Matrix4();
		btHingeConstraint hingeC = null;
		btConeTwistConstraint coneC = null;

		// PelvisSpine
		localA.setFromEulerAngles(0, PI2, 0).trn(0, 0.15f, 0);
		localB.setFromEulerAngles(0, PI2, 0).trn(0, -0.15f, 0);
		constraints.add(hingeC = new btHingeConstraint(pelvis, spine, localA, localB));
		hingeC.setLimit(-PI4, PI2);
//		hingeC.setBreakingImpulseThreshold(3000f);
		((btDynamicsWorld) world.collisionWorld).addConstraint(hingeC, true);

		// SpineHead
		localA.setFromEulerAngles(PI2, 0, 0).trn(0, 0.3f, 0);
		localB.setFromEulerAngles(PI2, 0, 0).trn(0, -0.14f, 0);
		constraints.add(coneC = new btConeTwistConstraint(spine, head, localA, localB));
		coneC.setLimit(PI4, PI4, PI2);
//		coneC.setBreakingImpulseThreshold(3000f);
		((btDynamicsWorld) world.collisionWorld).addConstraint(coneC, true);

		// LeftHip
		localA.setFromEulerAngles(-PI4 * 5f, 0, 0).trn(-0.18f, -0.1f, 0);
		localB.setFromEulerAngles(-PI4 * 5f, 0, 0).trn(0, 0.225f, 0);
		constraints.add(coneC = new btConeTwistConstraint(pelvis, leftupperleg, localA, localB));
		coneC.setLimit(PI4, PI4, 0);
//		coneC.setBreakingImpulseThreshold(300000f);
		((btDynamicsWorld) world.collisionWorld).addConstraint(coneC, true);

		// LeftKnee
		localA.setFromEulerAngles(0, PI2, 0).trn(0, -0.225f, 0);
		localB.setFromEulerAngles(0, PI2, 0).trn(0, 0.185f, 0);
		constraints.add(hingeC = new btHingeConstraint(leftupperleg, leftlowerleg, localA, localB));
		hingeC.setLimit(0, PI2);
//		hingeC.setBreakingImpulseThreshold(300000f);
		((btDynamicsWorld) world.collisionWorld).addConstraint(hingeC, true);

		// RightHip
		localA.setFromEulerAngles(-PI4 * 5f, 0, 0).trn(0.18f, -0.1f, 0);
		localB.setFromEulerAngles(-PI4 * 5f, 0, 0).trn(0, 0.225f, 0);
		constraints.add(coneC = new btConeTwistConstraint(pelvis, rightupperleg, localA, localB));
		coneC.setLimit(PI4, PI4, 0);
//		coneC.setBreakingImpulseThreshold(300000f);
		((btDynamicsWorld) world.collisionWorld).addConstraint(coneC, true);

		// RightKnee
		localA.setFromEulerAngles(0, PI2, 0).trn(0, -0.225f, 0);
		localB.setFromEulerAngles(0, PI2, 0).trn(0, 0.185f, 0);
		constraints.add(hingeC = new btHingeConstraint(rightupperleg, rightlowerleg, localA, localB));
		hingeC.setLimit(0, PI2);
//		hingeC.setBreakingImpulseThreshold(300000f);
		((btDynamicsWorld) world.collisionWorld).addConstraint(hingeC, true);

		// LeftShoulder
		localA.setFromEulerAngles(PI, 0, 0).trn(-0.2f, 0.15f, 0);
		localB.setFromEulerAngles(PI2, 0, 0).trn(0, -0.18f, 0);
		constraints.add(coneC = new btConeTwistConstraint(pelvis, leftupperarm, localA, localB));
		coneC.setLimit(PI2, PI2, 0);
//		coneC.setBreakingImpulseThreshold(300000f);
		((btDynamicsWorld) world.collisionWorld).addConstraint(coneC, true);

		// LeftElbow
		localA.setFromEulerAngles(0, PI2, 0).trn(0, 0.18f, 0);
		localB.setFromEulerAngles(0, PI2, 0).trn(0, -0.14f, 0);
		constraints.add(hingeC = new btHingeConstraint(leftupperarm, leftlowerarm, localA, localB));
		hingeC.setLimit(0, PI2);
//		hingeC.setBreakingImpulseThreshold(300000f);
		((btDynamicsWorld) world.collisionWorld).addConstraint(hingeC, true);

		// RightShoulder
		localA.setFromEulerAngles(PI, 0, 0).trn(0.2f, 0.15f, 0);
		localB.setFromEulerAngles(PI2, 0, 0).trn(0, -0.18f, 0);
		constraints.add(coneC = new btConeTwistConstraint(pelvis, rightupperarm, localA, localB));
		coneC.setLimit(PI2, PI2, 0);
//		coneC.setBreakingImpulseThreshold(300000f);
		((btDynamicsWorld) world.collisionWorld).addConstraint(coneC, true);

		// RightElbow
		localA.setFromEulerAngles(0, PI2, 0).trn(0, 0.18f, 0);
		localB.setFromEulerAngles(0, PI2, 0).trn(0, -0.14f, 0);
		constraints.add(hingeC = new btHingeConstraint(rightupperarm, rightlowerarm, localA, localB));
		hingeC.setLimit(0, PI2);
//		hingeC.setBreakingImpulseThreshold(300000f);
		((btDynamicsWorld) world.collisionWorld).addConstraint(hingeC, true);

		// fixConstraint = new btPoint2PointConstraint(spine, new Vector3(0, 1f,
		// 0f));
		// ((btDynamicsWorld)
		// world.collisionWorld).addConstraint(fixConstraint);
	}

	protected Model createCapsuleModel(float radius, float height) {
		final Model result = modelBuilder.createCapsule(radius, height + radius * 2f, 16,
				new Material(ColorAttribute.createDiffuse(Color.WHITE), ColorAttribute.createSpecular(Color.WHITE)),
				Usage.Position | Usage.Normal);
		disposables.add(result);
		return result;
	}
}