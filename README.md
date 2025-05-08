# Vigie des Médias – Projet Java

Ce projet a été développé dans le cadre d’un exercice académique visant à modéliser et simuler un système de surveillance des interactions médiatiques. Il repose sur une architecture orientée objet en Java, avec une approche modulaire et événementielle.

## Objectif

L'application permet de simuler des événements dans le domaine des médias (tels que des publications ou des rachats), et de déclencher des alertes via des modules spécialisés qui analysent ces événements en temps réel. Un module central, la Vigie, centralise et affiche les alertes détectées.

## Fonctionnalités

- Création d’entités : personnes physiques, personnes morales (organisations), et médias
- Système d’événements : publication, rachat de parts
- Modules spécialisés configurables :
  - Suivi des publications mentionnant des personnes surveillées
  - Suivi des rachats concernant des médias surveillés
- Transmission d’alertes à un module central (Vigie)
- Interface utilisateur en ligne de commande

## Structure du projet

```
src/
├── model/               → Modèles de données (Entite, Media, Evenement, etc.)
├── core/                → Composants principaux : vigie, dispatcher, services
├── modules/             → Modules spécialisés observateurs
├── ui/                  → Interface utilisateur en console
└── Main.java            → Point d’entrée de l’application
```

## Technologies utilisées

- Java SE
- Architecture orientée objet
- Git et GitHub
- Visual Studio Code

## Auteur

- [Ton prénom et nom ici]

Projet réalisé à titre académique. Toute réutilisation doit être accompagnée d’une mention de l’auteur original.

## Licence

Ce dépôt est destiné à un usage pédagogique uniquement. Aucune licence commerciale ou open source formelle n’est attachée.
