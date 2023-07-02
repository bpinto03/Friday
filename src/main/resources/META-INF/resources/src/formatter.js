
function translate(event){
  var key_map = {
    "eventId" : "id",
    "userId" : "userId",
    "title" : "title",
    "description" : "description",
    "location" : "location",
    "start" : "start",
    "end" : "end",
    "recurrency" : "rrule",
    "extendedProps" : "extendedProps" 
  }

  event["extendedProps"] = {... event}

  Object.keys(event).forEach(function(key) {
    var newKey = key_map[key]
    if(event[key] != null){
      event[newKey] = event[key]
    }
    if(newKey != key || event[key] == null){
      delete event[key]
    }

    if(event["rrule"] == ""){
      delete event["rrule"]
    }
  })
}


export default function keyMapper(content, xhr){
    // final array of transformed events
    var tr_events = []

    // loop through fetched array of events
    for (const event of content){
      translate(event)

      tr_events.push(event)
    }
    return tr_events
}
