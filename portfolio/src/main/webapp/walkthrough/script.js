async function fetchWalkthroughData() {
    const commentLimit = document.getElementById("comment_limit").value;
    const fetchedDataResponse = await fetch(`/data?comment_limit=${commentLimit}`);
    const fetchedDataJSON = await fetchedDataResponse.json();
    const contentList = document.getElementById("walkthrough-content");
    contentList.innerHTML = "";
    for (let i = 0; i < fetchedDataJSON.length; i++) {
        let listItem = document.createElement("li");
        listItem.innerText = fetchedDataJSON[i];
        contentList.appendChild(listItem);
    }
}

async function deleteComments() {
  const response = await fetch('/delete-data', {
    method: "POST"
  });
  fetchWalkthroughData();
}