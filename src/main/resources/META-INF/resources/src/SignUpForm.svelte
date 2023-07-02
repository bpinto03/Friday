<script>
    import { onMount } from "svelte";
    export let userId

    let username = ""
    let password = ""
    let passwordConfirmation = ""

    let alreadyTaken = false
    let notMatchingPassword = false
    let acceptSubmit = false

    let handleSubmit = () => {
        alreadyTaken = false
        acceptSubmit = false
        notMatchingPassword = false

        if (password !== passwordConfirmation){
            notMatchingPassword = true
            return
        }

        fetch(
            '/friday/newUser', {
            method: 'post',
            headers: {
                'Accept': '*/*',
                'Content-type' : 'application/json'
            },
            body: JSON.stringify({'username': username, 'password': password})
        })
        .then(res => {if(res.ok){acceptSubmit = true} else {alreadyTaken = true}})
    }
    

// TODO : CSS on this component

</script>

{#if userId == 0} 
    <form on:submit|preventDefault={handleSubmit} class="bg-[#d1d5db] mx-auto rounded-lg flex-none flex flex-col justify-center items-center py-6 border-2 w-96">
        <div class='space-x-2 mb-1'>
            <label for="username">
                Username
            </label>
            <input type="text" placeholder="Username" name="username" required bind:value={username} class="grow bg-[#e5e7eb]"/>
        </div>
        <div class='space-x-3 mt-1'>
            <label for="password">
                Password
            </label>
            <input type="password" placeholder="Password" name="password" required bind:value={password} class="grow bg-[#e5e7eb]"/>
        </div>
        <div class='space-x-2 mt-2'>
            <label for="password">
                Confirm<br>Password
            </label>
            <input type="password" placeholder="Password" name="password" required bind:value={passwordConfirmation} class="grow bg-[#e5e7eb]"/>
        </div>
        <div class="bg-[#9ca3af] rounded mt-6">
            <button type="submit">Log In</button>
        </div>
        {#if alreadyTaken}
            <p class="text-red-600"> Username is already taken by another user </p>
        {/if}
        {#if notMatchingPassword}
            <p class="text-red-600"> Passwords are not matching </p>
        {/if}
        {#if acceptSubmit}
            <p class="text-green-600"> User succesfully created </p>
        {/if}
    </form>

{/if}
