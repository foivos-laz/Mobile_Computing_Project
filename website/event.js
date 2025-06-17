import { initializeApp } from "https://www.gstatic.com/firebasejs/10.12.0/firebase-app.js";
import { getFirestore, doc, getDoc, updateDoc } from "https://www.gstatic.com/firebasejs/10.12.0/firebase-firestore.js";
import { getAuth, onAuthStateChanged } from "https://www.gstatic.com/firebasejs/10.12.0/firebase-auth.js";

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
const auth = getAuth(app);
const db = getFirestore(app);

const urlParams = new URLSearchParams(window.location.search);
const eventId = urlParams.get("id");

if (!eventId) {
  alert("ID d'événement manquant !");
} else {
  const eventRef = doc(db, "events", eventId);

  getDoc(eventRef).then(async (docSnap) => {
    if (!docSnap.exists()) {
      alert("Événement introuvable !");
      return;
    }

    const eventData = docSnap.data();
    console.log("✅ FIREBASE DOC:", eventData);

    // Affichage de base
    document.getElementById("event-name").textContent = eventData.name;
    document.getElementById("event-seats").textContent = eventData.availableSeats;
    document.getElementById("event-image").src = eventData.imageURL;
    document.getElementById("event-description").textContent = eventData.description;
    document.getElementById("event-location").textContent = eventData.location;
    document.getElementById("event-date").textContent = new Date(eventData.date.seconds * 1000).toLocaleString();
    document.getElementById("event-price").textContent = eventData.price;

    // Bouton volontaire
    if (eventData.askVolunteer === true) {
      const volunteerBtn = document.createElement("button");
      volunteerBtn.textContent = "I want to be volunteer";
      volunteerBtn.className = "ticket-button";

      volunteerBtn.addEventListener("click", () => {
        onAuthStateChanged(auth, async (user) => {
          if (!user) return alert("⚠️ Connecte-toi pour devenir volontaire.");
          const userId = user.uid;

          const updatedVolunteers = Array.isArray(eventData.volunteers) ? [...eventData.volunteers] : [];
          if (!updatedVolunteers.includes(userId)) {
            updatedVolunteers.push(userId);
            try {
              await updateDoc(doc(db, "events", eventId), {
                volunteers: updatedVolunteers
              });
              alert("✅ Tu es maintenant volontaire !");
              displayVolunteers(updatedVolunteers);
            } catch (error) {
              console.error("❌ Erreur updateDoc :", error);
            }
          } else {
            alert("ℹ️ Tu es déjà inscrit comme volontaire.");
          }
        });
      });

      const container = document.getElementById("event-volunteer-container");
      if (container) container.appendChild(volunteerBtn);
    }

    // Appel initial pour afficher les volontaires
    displayVolunteers(eventData.volunteers || []);
  });
}

// 🧑‍🤝‍🧑 Fonction pour afficher les noms des volontaires
async function displayVolunteers(volunteers) {
  console.log("📢 Affichage des volontaires :", volunteers);
  const listContainer = document.getElementById("volunteer-list-container");
  listContainer.innerHTML = "<h3>Liste des volontaires :</h3>";

  if (!volunteers || volunteers.length === 0) {
    listContainer.innerHTML += "<p>Aucun volontaire pour l’instant.</p>";
    return;
  }

  const ul = document.createElement("ul");

  for (const uid of volunteers) {
    try {
      const userDoc = await getDoc(doc(db, "users", uid));
      const userData = userDoc.exists() ? userDoc.data() : null;
      const li = document.createElement("li");
      li.textContent = `${userData?.name || "Utilisateur inconnu"}`;

      ul.appendChild(li);
    } catch (err) {
      console.error("⚠️ Erreur récupération utilisateur :", uid, err);
    }
  }

  listContainer.appendChild(ul);
}
