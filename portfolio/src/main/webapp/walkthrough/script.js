async function fetchWalkthroughData() {
    const fetchedDataResponse = await fetch("/data");
    const fetchedDataText = await fetchedDataResponse.text();
    const contentElement = document.getElementById("walkthrough-content");
    contentElement.innerText = fetchedDataText;
}