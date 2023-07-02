<script>
    import FullCalendar from 'svelte-fullcalendar';
    import timeGridPlugin from '@fullcalendar/timegrid';
    import dayGridPlugin from '@fullcalendar/daygrid';
    import keyMapper from "./formatter";
    import rrulePlugin from '@fullcalendar/rrule'
    export let userId


    let options = {
        timeZone: 'UTC'+1,
        initialView :'dayGridMonth', 
        plugins : [dayGridPlugin, timeGridPlugin, rrulePlugin],
        height : 700,
        events : {
            url: '/friday/getEventsFromUserId/' + userId,
            method: 'GET',
            extraParams : {

            },
            failure: function() {
                alert('Cannot fetch events for this user.');
            }
        },

        // function parsing API responses 
        eventSourceSuccess :keyMapper
    }   

</script>

<FullCalendar {options}  class="mx-64 mb-32"/>
