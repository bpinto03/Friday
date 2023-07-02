<script>
	//export let name;
	import Header from "./Header.svelte";
	import DayGrid from "./DayGrid.svelte";
	import MonthGrid from "./MonthGrid.svelte";
	import Menu from "./Menu.svelte"
	import LoginForm from "./LoginForm.svelte";
	import SignUpForm from "./SignUpForm.svelte";
	import IcsForm from "./IcsForm.svelte";
	import AddEventForm from "./AddEventForm.svelte";

	const displayEnum = Object.freeze({"info":1, "dayGrid":2, "monthGrid":3, "login":4, "signup":5, "ics":6, "addevent":7})

	var display = displayEnum.login
	var menuDisplayChoice = displayEnum.login

	// userId of 0 means user is not connected yet
	var userId = 0

	$: display = menuDisplayChoice
</script>

<Header/>

<Menu bind:displayChoice={menuDisplayChoice} bind:userId={userId}/>

{#if display == displayEnum.dayGrid}
<DayGrid bind:userId={userId}/>
{/if}

{#if display == displayEnum.info}
Coming soon ...
{/if}

{#if display == displayEnum.monthGrid}
<MonthGrid bind:userId={userId}/>
{/if}

{#if display == displayEnum.login}
<LoginForm bind:userId={userId}/>
{/if}

{#if display == displayEnum.signup}
<SignUpForm bind:userId={userId}/>
{/if}

{#if display == displayEnum.ics}
<IcsForm bind:userId={userId}/>
{/if}

{#if display == displayEnum.addevent}
<AddEventForm bind:userId={userId}/>
{/if}	





