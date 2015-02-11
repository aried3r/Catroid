/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2014 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * An additional term exception under section 7 of the GNU Affero
 * General Public License, version 3, is available at
 * http://developer.catrobat.org/license_additional_term
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.content.bricks;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.TextView;

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ExtendedActions;

import java.util.List;

public class KodeyMotorStopBrick extends BrickBaseType implements OnItemSelectedListener {
	private static final long serialVersionUID = 1L;
	private transient Motor motorEnum;
	private String motor;
	private transient AdapterView<?> adapterView;

	public static enum Motor {
		MOTOR_A, MOTOR_B, ALL_MOTORS
	}

	public KodeyMotorStopBrick(Motor motor) {
		this.motorEnum = motor;
		this.motor = motorEnum.name();
	}

	protected Object readResolve() {
		if (motor != null) {
			motorEnum = Motor.valueOf(motor);
		}
		return this;
	}

	@Override
	public int getRequiredResources() {
		return BLUETOOTH_KODEY;
	}

	@Override
	public Brick copyBrickForSprite(Sprite sprite) {
		KodeyMotorStopBrick copyBrick = (KodeyMotorStopBrick) clone();
		return copyBrick;
	}

	@Override
	public View getPrototypeView(Context context) {
		View prototypeView = View.inflate(context, R.layout.brick_kodey_motor_stop, null);
		Spinner kodeySpinner = (Spinner) prototypeView.findViewById(R.id.brick_kodey_stop_motor_spinner);
		kodeySpinner.setFocusableInTouchMode(false);
		kodeySpinner.setFocusable(false);

		ArrayAdapter<CharSequence> motorAdapter = ArrayAdapter.createFromResource(context,
				R.array.brick_kodey_stop_motor_spinner, android.R.layout.simple_spinner_item);
		motorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		kodeySpinner.setAdapter(motorAdapter);
		kodeySpinner.setSelection(motorEnum.ordinal());
		return prototypeView;
	}

	@Override
	public Brick clone() {
		return new KodeyMotorStopBrick(motorEnum);
	}

	@Override
	public View getView(Context context, int brickId, BaseAdapter baseAdapter) {
		if (animationState) {
			return view;
		}
		if (view == null) {
			alphaValue = 255;
		}
		view = View.inflate(context, R.layout.brick_kodey_motor_stop, null);
		view = getViewWithAlpha(alphaValue);

		setCheckboxView(R.id.brick_kodey_motor_stop_checkbox);
		final Brick brickInstance = this;
		checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				checked = isChecked;
				adapter.handleCheck(brickInstance, isChecked);
			}
		});

		ArrayAdapter<CharSequence> motorAdapter = ArrayAdapter.createFromResource(context,
				R.array.brick_kodey_stop_motor_spinner, android.R.layout.simple_spinner_item);
		motorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		Spinner motorSpinner = (Spinner) view.findViewById(R.id.brick_kodey_stop_motor_spinner);
		motorSpinner.setOnItemSelectedListener(this);

		if (!(checkbox.getVisibility() == View.VISIBLE)) {
			motorSpinner.setClickable(true);
			motorSpinner.setEnabled(true);
		} else {
			motorSpinner.setClickable(false);
			motorSpinner.setEnabled(false);
		}

		motorSpinner.setAdapter(motorAdapter);
		motorSpinner.setSelection(motorEnum.ordinal());
		return view;
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
		motorEnum = Motor.values()[position];
		motor = motorEnum.name();
		adapterView = parent;
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
	}

	@Override
	public View getViewWithAlpha(int alphaValue) {

		if (view != null) {

			View layout = view.findViewById(R.id.brick_kodey_motor_stop_layout);
			Drawable background = layout.getBackground();
			background.setAlpha(alphaValue);

			TextView textKodeyMotorStopLabel = (TextView) view.findViewById(R.id.ValueTextView);
			textKodeyMotorStopLabel.setTextColor(textKodeyMotorStopLabel.getTextColors().withAlpha(alphaValue));
			Spinner motorSpinner = (Spinner) view.findViewById(R.id.brick_kodey_stop_motor_spinner);
			ColorStateList color = textKodeyMotorStopLabel.getTextColors().withAlpha(alphaValue);
			motorSpinner.getBackground().setAlpha(alphaValue);
			if (adapterView != null) {
				((TextView) adapterView.getChildAt(0)).setTextColor(color);
			}

			this.alphaValue = (alphaValue);

		}

		return view;
	}

	@Override
	public List<SequenceAction> addActionToSequence(Sprite sprite, SequenceAction sequence) {
		sequence.addAction(ExtendedActions.kodeyMotorStopAction(motorEnum));
		return null;
	}

}
