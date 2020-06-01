async function fetchWalkthroughData() {
    const fetchedDataResponse = await fetch("/data");
    const fetchedDataJSON = await fetchedDataResponse.json();
    const contentList = document.getElementById("walkthrough-content");
    for (let i = 0; i < fetchedDataJSON.length; i++) {
        let listItem = document.createElement("li");
        listItem.innerText = fetchedDataJSON[i];
        contentList.appendChild(listItem);
    }
}
