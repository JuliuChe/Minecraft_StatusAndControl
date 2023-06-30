// Copyright (C) 2017 Hes-so Valais//Wallis, HEI, Sion. All rights reserved.
//
// Author: Patrice Rudaz (poatrice.rudaz@hevs.ch)

// *****  *************************************************************
let wSocket = null;

function connect() {         
	wSocket = new WebSocket("ws://localhost:8888");
	if (wSocket != null) {
		wSocket.onmessage = onMessage;
		wSocket.onopen    = function () {
			updateWelcomeMsg("Connected !", "darkGreen");
		};
		wSocket.onclose   = function (evt) {
			updateWelcomeMsg("Connection closed!", "darkGray");
		};
		wSocket.onerror   = function (evt) {
			updateWelcomeMsg("Connection error!", "#dd0000");
		};
	}
}

function onMessage(evt) {
	let str = evt.data;
	if (str.startsWith("Welcome")) {
		updateWelcomeMsg(str, "darkGray");
		return;
	}
	
	if (str.includes("=")) {
		let arrCmd     = str.split("=");
		let cmdLabel = arrCmd[0];
		let cmdValue = arrCmd[1];
		let factor   = 1;
		let offset   = 0;

		let elem     = document.getElementById(cmdLabel);

		if (elem != null) {


			// SOLAR_CONNECT_ST & WIND_CONNECT_ST
			if(cmdLabel.endsWith("CONNECT_ST")) {
				// Checks if value is a number
				if (isNaN(cmdValue)) {
					elem.className = (cmdValue.toLowerCase().localeCompare("true") === 0 ? "led led-green":"led led-red");
					elem.value = (cmdValue.toLowerCase().localeCompare("true") === 0 ? "ON":"OFF");
				} else {
					elem.className = (cmdValue == 1.0 ? "led led-green":"led led-red");
					elem.value = (cmdValue == 1.0 ? "ON":"OFF");
				}
				return;
			} 

			// FACTORY_ST & COAL_ST
			if(cmdLabel.endsWith("_ST")) {
				// Checks if value is a number
				if (!isNaN(cmdValue)) {
					let val =  Math.round(parseFloat(cmdValue) * 100).toFixed(0);

					// Update the Gauge
					if(cmdLabel.toLowerCase().includes("coal")) {
						updateCoalSp(val);
					} else {
						updateFactorySp(val);
					}
				}
				return;
			}

			// Remote Set Points and Switch
			if (cmdLabel.startsWith("REMOTE_")) {
				
				// Remote Set Points
				if (cmdLabel.endsWith("_SP")) {
					// Checks if the value is a number
					if (!isNaN(cmdValue)) {
						let val = Math.round(parseFloat(cmdValue) * 100).toFixed(0);
	
						// Update remote slider
						let slider = document.getElementById(cmdLabel);
						if (slider != null) {
							slider.value = val;
							slider.style.setProperty('--val', + (slider.value * 1.15));
						}
					}
					return;
				}
				
				// Remote Switch			
				if (cmdLabel.endsWith("_SW")) {
					// Checks if value is a number
					if (isNaN(cmdValue)) {
						elem.checked = (cmdValue.toLowerCase().localeCompare("true") === 0 ? true:false);
					} else {
						elem.checked = (cmdValue == 1.0 ? true:false);
					}
					return;				
				}
			}

			// All other Datapoints
			let val = ((parseFloat(cmdValue) * factor) + offset).toFixed(2);
			if (cmdLabel.includes("_U_")) {
				val += " [V]";
			} else if (cmdLabel.includes("_P_")) {
				val += " [W]";
			} else {
				val = Math.round(parseFloat(cmdValue) * 100).toFixed(0) + " %";
			}
			elem.value = val;
		}
	}
}

function updateWelcomeMsg(msg, color) {
	let welcome = document.getElementById("wsMessage");
	if (welcome != null) {
		welcome.style.color = color;
		welcome.innerHTML   = msg;
	}
}

function updateLog(msg, color) {
	let spanLog = document.getElementById("log");
	if (spanLog != null) {
		spanLog.style.color = color;
		spanLog.innerHTML   = msg;
	}
}

function toggleSwitch(cb) {
	if (cb != null) {
		let label = cb.id;
		let value = cb.checked;
		wSocket.send(label + "=" + value);
	}
}

function sliderUpdated(sl) {
	if (sl != null && wSocket != null) {
		let label = sl.id;
		let value = sl.value / 100;
		wSocket.send(label + "=" + value);
	}
}

function sliderChanged(sl) {
	sl.style.setProperty('--val', + sl.value);
}
