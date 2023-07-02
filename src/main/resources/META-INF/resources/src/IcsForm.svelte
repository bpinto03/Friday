<script>
    export let userId

    let rejectSubmit = false
    let acceptSubmit = false
    let url = ""
    //let file;

    let handleSubmitUrl = () => {
        fetch(
            '/friday/iCalendarUrlFile/' + userId + '?url=' + url, {
            method: 'get',
            headers: {
                'Accept': '*/*',
            },
        })
        .then(res => {rejectSubmit = false; acceptSubmit = false; if(res.ok){acceptSubmit = true} else {rejectSubmit = true}})
    }

    let handleSubmitFile = () => {
        fetch(
            '/friday/iCalendarFile/' + userId, {
            method: 'post',
            headers: {
                'Accept': '*/*',
                'Content-type' : 'multipart/form-data'
            },
            body:file
        })
        .then(res => {rejectSubmit = false; acceptSubmit = false; if(res.ok){acceptSubmit = true} else {rejectSubmit = true}})
    }
</script>


<form on:submit|preventDefault={handleSubmitUrl} class="bg-[#d1d5db] mx-auto rounded-lg flex-none flex flex-col justify-center items-center py-6 border-2 w-96">
    <div class='space-x-2 mb-2'>
        <label for="url">
            Url of .ics file
        </label>
        <input type="url" placeholder="url" name="url" required bind:value={url} class="grow bg-[#e5e7eb]"/>
    </div>
    <div class="bg-[#9ca3af] rounded mt-6">
        <button type="submit">import</button>
    </div>
    {#if acceptSubmit}
        <p class="text-green-600"> Imported Icalendar ✅ </p>
    {/if}
    {#if rejectSubmit}
        <p class="text-red-600"> Could not import ics file ❌ </p>
    {/if}
</form>
    
<!-- <form on:submit|preventDefault={handleSubmitFile} class="bg-[#d1d5db] mx-auto rounded-lg flex-none flex flex-col justify-center items-center py-6 border-2 w-96">
    <div class='space-x-2 mb-2'>
        <label for="file">
            Import file locally
        </label>
        <input type="file" name="file" required bind:value={file} class="grow bg-[#e5e7eb]"/>
    </div>
    <div class="bg-[#9ca3af] rounded mt-6">
        <button type="submit">import</button>
    </div>
    {#if acceptSubmit}
        <p class="text-green-600"> Imported Icalendar ✅ </p>
    {/if}
    {#if rejectSubmit}
        <p class="text-red-600"> Could not import ics file ❌ </p>
    {/if}
</form> -->
