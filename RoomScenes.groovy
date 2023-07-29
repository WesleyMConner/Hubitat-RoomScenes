import com.hubitat.app.DeviceWrapper as DeviceWrapper
import com.hubitat.app.DeviceWrapperList as DeviceWrapperList
import com.hubitat.hub.domain.Event as Event
import com.hubitat.hub.domain.Hub as Hub
#include wesmc.UtilsLibrary

definition(
  name: "RoomScenes",
  namespace: 'wesmc',
  author: 'Wesley M. Conner',
  description: "Define and Execute RA2-aware Scenes for a Hubitat Room",
  category: "",    // Not supported as of Q3'23
  iconUrl: "",     // Not supported as of Q3'23
  iconX2Url: "",   // Not supported as of Q3'23
  documentationLink: 'https://github.com/WesleyMConner/Hubitat-RoomScenes/README.adoc',
  importUrl: 'https://github.com/WesleyMConner/Hubitat-RoomScenes.git'
)

preferences {
  page(name: "monoPage", title: "", install: true, uninstall: true)
}

void addRoomObjToSettings() {
  // Abstract
  //   Ask client to select a single room and save the whole room
  //   object as "state.roomObj".
  // Design Notes
  //    There may not be an import for defining a RoomWrapper or a
  //    RoomWrapperList.
  ArrayList<LinkedHashMap> rooms = app.getRooms()
  List<Map<String, String>> roomPicklist = rooms
    .sort{ it.name }
    .collect{ [(it.id.toString()): it.name] }
  input(
    name: 'roomId',
    type: 'enum',
    title: 'Select the Room',
    submitOnChange: true,
    required: true,
    multiple: false,
    options: roomPicklist
  )
  if (settings.roomId) {
    state.roomObj = rooms.find{it.id.toString() == settings.roomId}
  }
}

Map monoPage() {
  return dynamicPage(
    name: "monoPage",
    title: "",
    install: true,
    uninstall: true
  ) {
    section {
      paragraph heading('Room Scenes')
      paragraph emphasis('Step 1: Identify the Hubitat Room this instance controls.')
      addRoomObjToSettings()
      if (state.roomObj) {
        paragraph emphasis('Step 2: Identify <b>at least</b> two Room Scenes.')
        paragraph bullet('<b>Step 2a:</b> (Optional) Use Hubitat Modes to name Room Scenes.')
//        state.roomObj = rooms.find{it.id.toString() == settings.roomId}
        // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        // !!! UNKNOWN IMPORT FOR ModeWrapper or ModeWrapperList !!!
        // !!!   Mode appears to have mode.id, mode.name, ...    !!!
        // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        ArrayList<LinkedHashMap> modes = location.modes
        List<String> locationNamePicklist = location.modes.collect{it.name}
        input(
          name: 'modesAsScenes',
          type: 'enum',
          title: 'Select the Modes',
          submitOnChange: true,
          required: false,
          multiple: true,
          options: locationNamePicklist
        )
        paragraph bullet('Step <b>2b:</b> (Optional) Create Custom Room Scene Name.')
        for (int i = 1; i<9; i++) {
          input(
            name: "cust${i}",
            type: 'text',
            title: "Custom Name",
            width: 3,
            submitOnChange: true,
            required: false,
            defaultValue: 'n/a'
          )
        }
      }
      List<String> scenes = (
        modesAsScenes.findAll{it}
        + [settings.cust1, settings.cust2, settings.cust3,
           settings.cust4, settings.cust5, settings.cust6,
           settings.cust7, settings.cust8].findAll{it && it != 'n/a'}
      ).sort{}
      paragraph bullet("<b>${scenes.size()} Scenes:</b> ${scenes.join(', ')}")
      if (scenes.size() >= 2) {
        paragraph emphasis('Proceed to Step 3?')
        input(
          name: 'proceedToDeviceSelection',
          type: 'bool',
          title: comment('Toggle to proceed.'),
          submitOnChange: true,
          required: true,
          defaultValue: false
        )
      }
      if (settings.proceedToDeviceSelection) {
        state.scenes = scenes
        ////
        //// EXIT
        ////
        input(
          name: 'exit',
          type: 'bool',
          title: comment('Exit?'),
          submitOnChange: true,
          required: true,
          defaultValue: false
        )
      }
    }
  }
}


/*
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
*/

// String addRoomHtml(LinkedHashMap room) {
//   return """<table>
//     ${roomHeadingsHtml(room.scenes)}
//     ${roomRowHtml(settings.devices[0], room.scenes)}
//   </table>"""
//   return """<table>
//     <td>Device Name</td>${roomHeadingsHtml(room.scenes)}
//     ${room.nonLutronId.collect{ d -> roomRowHtml(d, room.scenes) }}
//     ${room.mainRepId.collect{ d -> roomRowHtml(d, room.scenes) }}
//    </table>"""
// }




//      if (settings.roomObj) {
//        paragraph "roomObj: ${settings.roomObj}"
//      }

/*
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
              ${roomCellHtml(settings.devices[0], "BIRDY")}
        // Isolation test of roomRowHtml(...)
            ${roomHeadingsHtml(room.scenes)}
            ${roomRowHtml(settings.devices[0], room.scenes)}
*/
