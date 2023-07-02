<script>
    export let displayChoice;
    export let userId;

    let handleGoogleClick = () => {
        fetch(
            '/friday/GoogleCalendarCallback/' + userId, {
            method: 'GET',
            headers: {
                'Accept': '*/*',
            },
        }).then(res => {
            if(res.ok){
                alert("The google calendar was succesfully added.")
            }
            else {
                alert("Google calendar could not be fetched.")
            }
        })
        displayChoice = 1
    }
    
</script>


<div class="flex flex-row flex-none justify-center">
    {#if userId!==0} 
        <div class="flex-initial border-2 border-cyan-800 bg-sky-600 hover:bg-sky-700">
            <p class="px-8 py-2" on:click={() => displayChoice=1}>
                Check-Up ! <span class="text-2xl">📋</span>
            </p>
        </div>
        <div class="flex-initial border-2 border-cyan-800 bg-sky-600 hover:bg-sky-700">
            <p class="px-8 py-2" on:click={() => displayChoice=2}>
                Day View <span class="text-2xl">&#128197;</span>
            </p>
        </div>
        <div class="flex-initial border-2 border-cyan-800 bg-sky-600 hover:bg-sky-700">
            <p class="px-8 py-2" on:click={() => displayChoice=3}>
                Month View <span class="text-2xl">🗓️</span>
            </p>
        </div>
        <div class="flex-initial border-2 border-cyan-800 bg-sky-600 hover:bg-sky-700">
            <p class="px-8 py-2" on:click={() => displayChoice=7}>
                Add Event
            </p>
        </div> 
        <div class="flex-initial border-2 border-cyan-800 bg-sky-600 hover:bg-sky-700">
            <p class="px-8 py-2" on:click={handleGoogleClick}>
                Import Google Calendar
            </p>
        </div>
        <div class="flex-initial border-2 border-cyan-800 bg-sky-600 hover:bg-sky-700">
            <p class="px-8 py-2" on:click={() => displayChoice=6}>
                Import iCalendar
            </p>
        </div> 
    {/if}
    {#if userId==0}
        <div class="flex-initial border-2 border-cyan-800 bg-sky-600 hover:bg-sky-700">
            <p class="px-8 py-2" on:click={() => displayChoice=4}>
                Log in
            </p>
        </div>
        <div class="flex-initial border-2 border-cyan-800 bg-sky-600 hover:bg-sky-700">
            <p class="px-8 py-2" on:click={() => displayChoice=5}>
                Sign up
            </p>
        </div>
    {:else}
        <div class="flex-initial border-2 border-cyan-800 bg-sky-600 hover:bg-sky-700">
            <p class="px-8 py-2" on:click={() => {userId=0; displayChoice=4}}>
                Log out 
            </p>
        </div>
    {/if}
</div>