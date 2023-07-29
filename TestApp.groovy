import com.hubitat.app.DeviceWrapper as DeviceWrapper
import com.hubitat.app.DeviceWrapperList as DeviceWrapperList
import com.hubitat.hub.domain.Event as Event
import com.hubitat.hub.domain.Hub as Hub

// Design Notes
//   - Hubitat DOES NOT allow applications to leverages HTML tables for
//     input() layout.
//   - The "width" argument in input() allows for a 1..12 value that scales
//     the space allotted to each input.
//   - Accept adequate. Pretty is likely not an option. 

definition(
  name: "TestApp",
  namespace: "hubitat",
  author: "WesleyMC",
  description: "",
  category: "",
  iconUrl: "",
  iconX2Url: ""
)

preferences {
  page(name: "TestPage")
}

void deviceSceneInputs(DeviceWrapper d, List<String> scenes) { 
  scenes.collect{scene, index ->
    input(
      name: "${d.id}:${scene}",
      type: 'integer',
      width: 1,
      title: ${index == 1 ? d.displayName : ''},
      defaultValue: 0
    )
  }
}

String roomHtml(LinkedHashMap room) {
  return """<table>
    ${roomHeadingsHtml(room.scenes)}
    ${roomRowHtml(settings.devices[0], room.scenes)}
  </table>"""
/*
  return """<table>
    <td>Device Name</td>${roomHeadingsHtml(room.scenes)}
    ${room.nonLutronId.collect{ d -> roomRowHtml(d, room.scenes) }}
    ${room.mainRepId.collect{ d -> roomRowHtml(d, room.scenes) }}
  </table>"""
*/
}

Map TestPage() {
  return dynamicPage(
    name: "TestPage",
    title: "TestApp",
    install: false,
    uninstall: false
  ) {
    // Get actual non-Lutron devices.
    section {
      paragraph "settings keys are >${settings.keySet()}<"
      input (
        name: 'devices',
        type: 'capability.switch',
        title: 'Select Non-Lutron Switches',
        submitOnChange: true,
        required: true,
        multiple: true
      )
      input (
        name: 'repeaters',
        type: 'device.LutronKeypad',
        title: 'Select Lutron Main Repeaters',
        submitOnChange: true,
        required: true,
        multiple: true
      )
      if (settings.devices && settings.repeaters) {
        // Leverag types for coding.
        List<String> scenes = ['alpha', 'beta', 'gamma']
        Map<Integer, String> devices = settings.devices
        Map<Integer, String> repeaters = settings.repeaters
        Map<String, Integer> deviceValues = [:]
        Map<String, Integer> repeaterValues = [:]

        // Preserve room state
        state.room = [:]
        state.room.scenes = scenes
        state.room.devices = devices
        state.room.repeaters = repeaters
        state.room.deviceValues = deviceValues
        state.room.repeaterValues = repeaterValues

        // Isolation test of roomCellHtml(...)
        /* paragraph """
          <table>
            <tr>
              ${roomCellHtml(settings.devices[0], "BIRDY")}
            </tr>
          </table
        """ */
        // Isolation test of roomRowHtml(...)
        /* */ paragraph """
          <table>
            ${roomHeadingsHtml(room.scenes)}
            ${roomRowHtml(settings.devices[0], room.scenes)}
          </table>
        """ 
        input(name: "5672:alpha", type: "integer", width: 1, defaultValue: 0)
        
        input(name: "5672:alpha", type: "integer", width: 1, defaultValue: 0)
        
        input(name: "5672:alpha", type: "integer", width: 1, defaultValue: 0)
      }
    }
  }
}

// ------------------------------------------------------------------------
// Per https://community.hubitat.com/t/lutron-integrators/658/3
//   You need to add your main repeater to your Lutron Integration as a
//   keypad. It has integration id 1. Then it will become available for
//   selection in the app.
//     - capability.momentary
//     - capability.pushableButton
//     - capability.releasableButton
// ------------------------------------------------------------------------

// No signature of method: user_app_hubitat_TestApp_323.devicesAsHtml()
// is applicable for argument types: (java.util.LinkedHashMap)
// values: [
//   [
//     scenes:[alpha, beta, gamma],
//     devices:[Bev Station (63), Dining Table (09), ...], ..
//   ]
// ] on line 79 (method TestPage)