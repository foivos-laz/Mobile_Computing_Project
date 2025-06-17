
  import { initializeApp } from "https://www.gstatic.com/firebasejs/10.12.0/firebase-app.js";
  import { getFirestore, collection, getDocs } from "https://www.gstatic.com/firebasejs/10.12.0/firebase-firestore.js";
  const firebaseConfig = {
    apiKey: "AIzaSyALcuxor9TjHXL01IgSw52CkUg0ZXiFrmY",
    authDomain: "mobile-computing-project-2270c.firebaseapp.com",
    projectId: "mobile-computing-project-2270c",
    storageBucket: "mobile-computing-project-2270c.appspot.com",
    messagingSenderId: "1045840215156",
    appId: "1:1045840215156:web:b57f229cfa2bf7207e6521",
    measurementId: "G-GWT8VKKNJK"
  };

  const app = initializeApp(firebaseConfig);
  const db = getFirestore(app);

  // 🔽 Récupère les événements (par exemple les 3 derniers)
  const eventRef = collection(db, "events");

getDocs(collection(db, "events")).then((querySnapshot) => {
  const gallery = document.getElementById("event-gallery");

  querySnapshot.forEach((doc) => {
    const event = doc.data();
    const eventId = doc.id;

    // Création de la carte
    const card = document.createElement("div");
    card.className = "event-card";

    const link = document.createElement("a");
    link.href = `Event.html?id=${eventId}`;

    const img = document.createElement("img");
    img.src = event.imageURL;
    img.alt = event.name;

    const title = document.createElement("h3");
    title.textContent = event.name;

    link.appendChild(img);
    card.appendChild(link);
    card.appendChild(title);
    gallery.appendChild(card);
  });
});
