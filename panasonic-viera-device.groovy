/**
 *  SmartThings Device Handler: Panasonic Viera
 *
 *  Author: konni@konni.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 */
metadata {
  definition (name: "Panasonic Viera TV", namespace: "com.konni.smartthings", author: "konni@konni.com") {

    /**
     * List our capabilities. Doing so adds predefined command(s) which
     * belong to the capability.
     */
   
    capability "Music Player"
    capability "Switch"
    capability "Switch Level"
    capability "Refresh"
    capability "Polling"
    capability "Actuator"

    /**
     * Define all commands, ie, if you have a custom action not
     * covered by a capability, you NEED to define it here or
     * the call will not be made.
     *
     * To call a capability function, just prefix it with the name
     * of the capability, for example, refresh would be "refresh.refresh"
     */
    command "mutedOn"
    command "mutedOff"
    
    command "av"
    
  }

  /**
   * Define the various tiles and the states that they can be in.
   * The 2nd parameter defines an event which the tile listens to,
   * if received, it tries to map it to a state.
   *
   * You can also use ${currentValue} for the value of the event
   * or ${name} for the name of the event. Just make SURE to use
   * single quotes, otherwise it will only be interpreted at time of
   * launch, instead of every time the event triggers.
   */
  tiles(scale: 2) {
    multiAttributeTile(name:"state", type:"lighting", width:6, height:4) {
      tileAttribute ("device.switch", key: "PRIMARY_CONTROL") {
        attributeState "on", label:'On', action:"switch.off", icon:"st.Electronics.electronics16", backgroundColor:"#79b821", nextState:"off"
        attributeState "off", label:'Off', action:"switch.on", icon:"st.Electronics.electronics16", backgroundColor:"#ffffff", nextState:"on"
      }
      tileAttribute ("source", key: "SECONDARY_CONTROL") {
        attributeState "source", label:'${currentValue}'
      }
    }

    // row
    controlTile("volume", "device.volume", "slider", height: 1, width: 6, range:"(0..100)") {
      state "volume", label: "Volume", action:"music Player.setLevel", backgroundColor:"#00a0dc"
    }

    // row
    standardTile("muted", "device.muted", decoration: "flat", width: 2, height: 2) {
      state("off", label:'Muted', action:"mutedOn", icon:"https://raw.githubusercontent.com/redloro/smartthings/master/images/indicator-dot-gray.png", backgroundColor:"#ffffff", nextState:"on")
      state("on", label:'Muted', action:"mutedOff", icon:"https://raw.githubusercontent.com/redloro/smartthings/master/images/indicator-dot-mute.png", backgroundColor:"#ffffff", nextState:"off")
    }
    
    standardTile("av", "device.av", width: 2, height: 2, inactiveLabel: false, decoration: "flat") {
      state "default", label:"AV", action:"av", icon:"st.secondary.refresh-icon", backgroundColor:"#ffffff"
    }
    
    standardTile("refresh", "device.status", width: 2, height: 2, inactiveLabel: false, decoration: "flat") {
      state "default", label:"Refresh", action:"refresh.refresh", icon:"st.secondary.refresh-icon", backgroundColor:"#ffffff"
    }

    // Defines which tile to show in the overview
    main "state"

    // Defines which tile(s) to show when user opens the detailed view
    details([
      "state",
      "volume",
      "muted","av","refresh"
    ])
  }

  
}

/**************************************************************************
 * The following section simply maps the actions as defined in
 * the metadata into onAction() calls.
 *
 * This is preferred since some actions can be dealt with more
 * efficiently this way. Also keeps all user interaction code in
 * one place.
 *
 */
def on() {
  sendCommand("NRC_POWER-ONOFF")
  sendEvent(name: "switch", value: "on")
}
def off() {
  sendCommand("NRC_POWER-ONOFF")
  sendEvent(name: "switch", value: "off")
}
def mutedOn() {
  sendCommand("NRC_MUTE-ONOFF")
  sendEvent(name: "muted", value: "on")
}
def mutedOff() {
  sendCommand("NRC_MUTE-ONOFF")
  sendEvent(name: "muted", value: "off")
}
def setLevel(value) {
  sendVolume(value)
  sendEvent(name: "volume", value: value)
}
def av() {
  sendCommand("NRC_CHG_INPUT-ONOFF")
}

def refresh() {
  log.debug "Refresh Called"
  // sendCommand("/${getZone()}/getStatus")
}
/**************************************************************************/

/**
 * Called every so often (every 5 minutes actually) to refresh the
 * tiles so the user gets the correct information.
 */
def poll() {
  refresh()
}

// parse events into attributes
def parse(String description) {
	log.debug "Virtual parsing '${description}'"
}

private sendVolume(volume) {
    log.debug "Sending Volume Command to Parent"
    parent.sendVolume(volume)
}

private sendCommand(command) {
  log.debug "Sending Command to Parent"
  parent.sendCommand(command)
}
