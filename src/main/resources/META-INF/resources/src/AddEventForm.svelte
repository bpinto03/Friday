<script>
    export let userId

    let title = ""
    let description = ""
    let location = ""
    let start = "" // date
    let end = "" // date

    let border_color = "slate"


    let handleCreate = () => {
        let body = {
            'userId' : userId,
            'title' : title,
            'description' : description,
            'location' : location,
            'start' : start,
            'end' : end,
        }

        fetch(
            '/friday/createEvent/', {
            method: 'post',
            headers: {
                'Accept': '*/*',
                'Content-type' : 'application/json'
            },
            body: JSON.stringify(body)
        })
        .then(res => {
            if(!res.ok){
                border_color = "red"
                alert("Event creation didn't go through. Please check Date formatting.")
            }else{
                border_color = "green"
            }
        })
    }
</script>

<!-- Tailwind won't load these unless we use them once -->
<div class="border-red-400"></div>
<div class="border-green-400"></div>


<form on:submit|preventDefault={handleCreate} class="h-min w-3/12 mt-7 mb-7 mx-auto border-2 g-white shadow-md rounded px-8 pt-6 pb-8 border-{border_color}-400" >
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
    <div class="flex items-center justify-center">
        <button type="submit" class="bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded focus:outline-none focus:shadow-outline">Create</button>
    </div>
</form>