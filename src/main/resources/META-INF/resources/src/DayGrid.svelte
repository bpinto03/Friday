<script>
    import FullCalendar from 'svelte-fullcalendar';
    import timeGridPlugin from '@fullcalendar/timegrid';
    import dayGridPlugin from '@fullcalendar/daygrid';
    import keyMapper from "./formatter";
    import rrulePlugin from '@fullcalendar/rrule'
    export let userId

    let dayGridRef;

    let eventId = 0

    let title = ""
    let description = ""
    let location = ""
    let start = "" // date
    let end = "" // date
    let reccurency = ""
    
    let handleModify = () => {
        if (eventId == 0){ return }

        let body = {
            'userId' : userId,
            'title' : title,
            'description' : description,
            'location' : location,
            'start' : start,
            'end' : end,
            'recurrency' : reccurency
        }

        fetch(
            '/friday/modifyEvent/' + eventId, {
            method: 'put',
            headers: {
                'Accept': '*/*',
                'Content-type' : 'application/json'
            },
            body: JSON.stringify(body)
        })
        .then(res => {
            if(res.ok){
                dayGridRef.getAPI().refetchEvents()
            } else {
                alert("Can't modify event. Please check formatting.")
            }})
    }

    let handleDelete = () => {
        fetch(
            '/friday/delete/' + eventId, {
            method: 'delete',
        }).then(res => {
            dayGridRef.getAPI().refetchEvents()
        })

        eventId = 0
    }

    let options = {
        timeZone: 'UTC'+1,
        initialView :'timeGridDay', 
        plugins : [dayGridPlugin, timeGridPlugin, rrulePlugin],
        height : 700,
        aspectRatio: 2,
        events : {
            url: '/friday/getEventsFromUserId/' + userId,
            method: 'GET',
            failure: function() {
                alert('Cannot fetch events for this user.');
            }
        },

        eventContent: function(arg) {
            let arrayOfDomNodes = []

            if (arg.event.title != "") {
                let boldTitle = document.createElement('b')
                boldTitle.innerHTML = arg.event.title
                arrayOfDomNodes.push(boldTitle)
                arrayOfDomNodes.push(document.createElement("br"))
            }

            if (arg.event.extendedProps.location != "") {
                let italicLocation = document.createElement('i')
                italicLocation.innerHTML = "    📍 " + arg.event.extendedProps.location
                arrayOfDomNodes.push(italicLocation)
                arrayOfDomNodes.push(document.createElement("br"))
            }

            if (arg.event.extendedProps.description != "") {
                let italicDescription = document.createElement('i')
                italicDescription.innerHTML = "    ☰ " + arg.event.extendedProps.description
                arrayOfDomNodes.push(italicDescription)
            }

            return { domNodes: arrayOfDomNodes }
        },

        eventClick : function(info){
            eventId = info.event.extendedProps.eventId
            title = info.event.extendedProps.title
            location = info.event.extendedProps.location
            description = info.event.extendedProps.description
            start = info.event.extendedProps.start
            end = info.event.extendedProps.end
            reccurency = info.event.extendedProps.reccurency
        },

        // function parsing API responses 
        eventSourceSuccess :keyMapper
    }   

</script>

<div class="flex flex-row border-2 border-slate-400">
    <FullCalendar bind:this="{dayGridRef}" {options}  class="basis-6/12 p-2 border-2 border-slate-400"/>

    <!-- TODO : CSS à faire --> 
    
    {#if eventId !== 0}
        <form on:submit|preventDefault={handleModify} class="h-min border-2 border-slate-400 g-white shadow-md rounded px-8 pt-6 pb-8 mb-4">
            <div class="mb-4">
                <label for="title" class="block text-gray-700 text-sm font-bold mb-2">
                    title
                </label>
                <input type="text" placeholder={title} name="title" bind:value={title} 
                class="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"/>
            </div>
            <div class="mb-4">
                <label for="description" class="block text-gray-700 text-sm font-bold mb-2">
                    description
                </label>
                <input type="text" placeholder={description} name="description" bind:value={description} 
                class="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"/>
            </div>
            <div class="mb-4">
                <label for="location" class="block text-gray-700 text-sm font-bold mb-2">
                    location
                </label>
                <input type="text" placeholder={location} name="location" bind:value={location} 
                class="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"/>
            </div>
            <div class="mb-4">
                <label for="start" class="block text-gray-700 text-sm font-bold mb-2">
                    start
                </label>
                <input type="datetime-local" placeholder={start} name="start" bind:value={start} 
                class="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"/>
            </div>
            <div class="mb-4">
                <label for="end" class="block text-gray-700 text-sm font-bold mb-2">
                    end
                </label>
                <input type="datetime-local" placeholder={end} name="end" bind:value={end} 
                class="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"/>
            </div>
            <div class="flex items-center justify-between">
                <button type="submit" class="bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded focus:outline-none focus:shadow-outline">Modify</button>
                <button type="button" class="bg-red-500 hover:bg-red-700 text-white font-bold py-2 px-4 rounded focus:outline-none focus:shadow-outline" on:click={handleDelete}>Delete</button>
            </div>
        </form>
    {/if}
</div>
