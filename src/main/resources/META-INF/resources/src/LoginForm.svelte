<script>
    import { onMount } from "svelte";
    export let userId

    let username = ""
    let password = ""

    let rejectSubmit = false

    onMount(() => {
        rejectSubmit = false
    })

    let handleSubmit = () => {
        fetch(
            '/friday/login', {
            method: 'post',
            headers: {
                'Accept': '*/*',
                'Content-type' : 'application/json'
            },
            body: JSON.stringify({'username': username, 'password': password})
        })
        .then(res => {if(res.ok){return res.json()} else {throw new Error('')}})
        .then(res => {userId = res})
        .catch(error => {rejectSubmit = true});
    }
    

// TODO : CSS on this component

</script>

{#if userId == 0}
    <form on:submit|preventDefault={handleSubmit} class="bg-[#d1d5db] mx-auto rounded-lg flex-none flex flex-col justify-center items-center py-6 border-2 w-96">
        <div class='space-x-2 mb-2'>
             <label for="username">
                Username
            </label>
            <input type="text" placeholder="Username" name="username" required bind:value={username} class="grow bg-[#e5e7eb]"/>
        </div>
        <div class='space-x-3 mt-4'>
            <label for="password">
                Password
            </label>
            <input type="password" placeholder="Password" name="password" required bind:value={password} class="grow bg-[#e5e7eb]"/>
        </div>
        <div class="bg-[#9ca3af] rounded mt-6">
            <button type="submit">Log In</button>
        </div>
        {#if rejectSubmit}
            <p class="text-red-600"> Username and/or password is wrong </p>
        {/if}
    </form>
    
{/if}
