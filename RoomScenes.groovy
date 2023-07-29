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

void addScenesToSettings (String heading) {
  paragraph heading \
    + '<b>Use Hubitat Modes to name Room Scenes</b> <em>..Optional</em>'
  // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
  // !!! UNKNOWN IMPORT FOR ModeWrapper or ModeWrapperList !!!
  // !!!   Mode appears to have mode.id, mode.name, ...    !!!
  // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
  ArrayList<LinkedHashMap> modes = location.modes
  state.modes = modes
  List<String> locationNamePicklist = state.modes.collect{it.name}
  input(
    name: 'modesAsScenes',
    type: 'enum',
    title: 'Select the Modes',
    submitOnChange: true,
    required: false,
    multiple: true,
    options: locationNamePicklist
  )
  paragraph '<b>Create Custom Room Scene Name</b> <em>..Optional</em>'
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
  List<String> scenes = (
    modesAsScenes.findAll{it}
    + [settings.cust1, settings.cust2, settings.cust3,
       settings.cust4, settings.cust5, settings.cust6,
       settings.cust7, settings.cust8].findAll{it && it != 'n/a'}
  ).sort{}
  state.scenes = scenes
}

void addModeIdToSceneToSettings (String heading) {
  // Abstract
  //   Ask client to select a scene for each current Hub mode and persist
  //   the results as "state.modeIdToScene" (a Map<String, String>).
  //
  // Design Notes
  //   - The mapping facilitates state.currentScene == 'AUTO'
  //   - Refresh the mapping if/when site modes are changed.
  paragraph(heading)
  Map<String, String> modeIdToRoomScene
  ArrayList<LinkedHashMap> modes = location.modes
  modes.each{mode ->
    Boolean modeNameIsSceneName = state.scenes.find{it == mode.name} ? true : false
    input(
      name: "${mode.id}ToScene",
      type: 'enum',
      title: "Scene for ${mode.name}",
      width: 2,
      submitOnChange: true,
      required: true,
      multiple: false,
      options: state.scenes,
      defaultValue: modeNameIsSceneName ? mode.name : ''
    )
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
      //--paragraph hubPropertiesAsHtml()
      paragraph heading('Room Scenes') \
        + important('<br/>Tab to register field changes !!!')
      addRoomObjToSettings(
        emphasis('Step 1: Identify the Hubitat Room this instance controls.')
      )
      if (state.roomObj) {
        addScenesToSettings (
          emphasis("Step 2: Identify Room Scenes for ${state.roomObj.name}.")
        )
      }
      paragraph bullet("<b>Current Scenes:</b> ${state.scenes.join(', ') ?: '...none...'}")
      if (state.scenes.size() < 2) {
        paragraph comment('At least two Room Scenes must be defined in order to proceed.')
      } else {
        addModeIdToSceneToSettings(
          emphasis('Step 3: Map Hub modes to Room Scenes for automation.')
        )
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
