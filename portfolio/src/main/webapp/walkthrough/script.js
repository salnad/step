async function fetchWalkthroughData() {
    const fetchedDataResponse = await fetch("/data?comment_limit=1");
    const fetchedDataJSON = await fetchedDataResponse.json();
    const contentList = document.getElementById("walkthrough-content");
    for (let i = 0; i < fetchedDataJSON.length; i++) {
        let listItem = document.createElement("li");
        listItem.innerText = fetchedDataJSON[i];
        contentList.appendChild(listItem);
    }
}

