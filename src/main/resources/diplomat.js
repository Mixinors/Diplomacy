function getMainBody() {
    return document.getElementById("main-body")
}

function getMainList() {
    return document.getElementById("main-list")
}

function getClearList() {
    let existingList = getMainList()

    let mainList = document.getElementById("main-list")

    if (existingList != null) {
        while(existingList.firstChild) existingList.removeChild(existingList.firstChild);
    } else {
        mainList = document.createElement("ul");
        mainList.classList.add("list-flat")
        mainList.id = "main-list"
    }

    return mainList
}

function getNationString(data) {
    return data.name + ", with code " + data.code + "."
}

function getProvinceString(data) {
    return data.name + " in " + data.owner.name + ", which " + ((data.center) ? ("is a Supply Center and ") : ("")) + " resides at " + data.type.toLowerCase() + "."
}

function getGroupString(data) {
    return data.type + " at " + data.location.province.name + ", in " + data.location.nation.name + ", owned by " + data.owner.name + ", with a strength of " + data.strength + ", and a cost of " + data.cost + "."
}

function getOrderString(data) {
    return data.groupType + " at " + data.location.province.name + " in " + data.location.nation.name + ", placed by " + data.owner.name + ", is to " + data.type.toLowerCase() +
        ((data.target.nation.name == "none") ? (".") :  (" with target " + data.target.province.name + ", in " + data.target.nation.name + "."))
}

function onClickWorld() {
    const body = getMainBody()

    const mainList = getClearList()

    fetch("http://localhost:443/world").then(response => response.json().then(data => {
        const nationItem = document.createElement("li")
        nationItem.appendChild(document.createTextNode("There " + ((data.nations.length > 1) ? ("are ") : ("is ")) + toWords(data.nations.length) + " nation" + ((data.nations.length > 1) ? "s" : "") + " in this world."))
        mainList.appendChild(nationItem)

        const provinceItem = document.createElement("li")
        provinceItem.appendChild(document.createTextNode("There " + ((data.provinces.length > 1) ? ("are ") : ("is ")) + toWords(data.provinces.length) + " province" + ((data.provinces.length > 1) ? "s" : "") + " in this world."))
        mainList.appendChild(provinceItem)

        const groupItem = document.createElement("li")
        groupItem.appendChild(document.createTextNode("There " + ((data.groups.length > 1) ? ("are ") : ("is ")) + toWords(data.groups.length) + " group" + ((data.groups.length > 1) ? "s" : "") + " in this world."))
        mainList.appendChild(groupItem)

        const orderItem = document.createElement("li");
        orderItem.appendChild(document.createTextNode("There " + ((data.orders.length > 1) ? ("are ") : ("is ")) +  toWords(data.orders.length) + " order" + ((data.orders.length > 1) ? "s" : "") + " in this world."))
        mainList.appendChild(orderItem)
    }))

    body.appendChild(mainList)
}

function onClickNations() {
    const body = getMainBody()

    const mainList = getClearList()

    fetch("http://localhost:443/nations").then(response => response.json().then(data => {
        data.forEach(nation => {
            const countryItem = document.createElement("li")
            countryItem.appendChild(document.createTextNode(getNationString(nation)))
            mainList.appendChild(countryItem)
        })
    }))

    body.appendChild(mainList)
}

function onClickProvinces() {
    const body = getMainBody()

    const mainList = getClearList()

    fetch("http://localhost:443/provinces").then(response => response.json().then(data => {
        data.forEach(province => {
            const item = document.createElement("li")
            item.appendChild(document.createTextNode(getProvinceString(province)))
            mainList.appendChild(item)
        })
    }))

    body.appendChild(mainList)
}

function onClickGroups() {
    const body = getMainBody()

    const mainList = getClearList()

    fetch("http://localhost:443/groups").then(response => response.json().then(data => {
        data.forEach(group => {
            const groupItem = document.createElement("li")
            groupItem.appendChild(document.createTextNode(getGroupString(group)))
            mainList.appendChild(groupItem)
        })
    }))

    body.appendChild(mainList)
}

function onClickOrders() {
    const body = getMainBody()

    const mainList = getClearList()

    fetch("http://localhost:443/orders").then(response => response.json().then(data => {
        data.forEach(order => {
            const orderItem = document.createElement("li")
            orderItem.appendChild(document.createTextNode(getOrderString(order)))
            mainList.appendChild(orderItem)
        })
    }))

    body.appendChild(mainList)
}
