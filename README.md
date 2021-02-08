[![Contributors][contributors-shield]][contributors-url]
[![Forks][forks-shield]][forks-url]
[![Stargazers][stars-shield]][stars-url]
[![Issues][issues-shield]][issues-url]
[![License][license-shield]][license-url]



<!-- PROJECT LOGO -->
<br />
<p align="center">
  <a href="https://github.com/aleksn41/corona_world_app">
    <img src="https://raw.githubusercontent.com/aleksn41/corona_world_app/master/app/src/main/ic_launcher-playstore.png" alt="Logo" width="128" height="128">
  </a>

  <h3 align="center">Corona World App</h3>

  <p align="center">
    A student Project made by <a href="https://github.com/Prom3thean"> Prom3thean</a> and <a href="https://github.com/aleksn41">aleksn41</a> for the University DHBW Mannheim.
    <!--
    <br />
    <a href="https://github.com/github_username/repo_name"><strong>Explore the docs »</strong></a>
      -->
    <br />
    <br />
    <a href="https://github.com/aleksn41/corona_world_app/issues">Report Bug</a>
    ·
    <a href="https://github.com/aleksn41/corona_world_app/issues">Request Feature</a>
  </p>
</p>



<!-- TABLE OF CONTENTS -->
<details open="open">
  <summary><h2 style="display: inline-block">Table of Contents</h2></summary>
  <ol>
    <li>
      <a href="#about-the-project">About The Project</a>
    </li>
    <li>
      <a href="#getting-started">Getting Started</a>
      <ul>
        <li><a href="#prerequisites">Prerequisites</a></li>
        <li><a href="#installation">Installation</a></li>
      </ul>
    </li>
    <li><a href="#usage">Usage</a></li>
    <!--
    <li><a href="#license">License</a></li>
    <li><a href="#contact">Contact</a></li>
    -->
    <li><a href="#APIs">APIs</a></li>
    <li><a href="#Libraries">Libraries</a></li>
  </ol>
</details>



<!-- Screenshoots can be added later -->
## About The Project

With the Corona World App you can learn all about the current Spread of the Corona Virus in the World.

Main Features:

- A **Heat Map** of the current Spread of the Virus all over the World for the current Day
- A more **in-depth look** into the current Situation of the Country by selecting it on the Map
- **User created Statistics**, where the User can get all kinds of different Data of the **current Day** or of **the Past**
- **History** of all your customized Statistics
- **Favourites** for your favourite custom made Statistic


<!-- GETTING STARTED -->
## Getting Started

You can either get the APK by going to the [releases](https://github.com/aleksn41/corona_world_app/releases) or by cloning this Repository and building a debug APK for yourself

### Prerequisites

This Application can only run on Android 10 or greater (API level 29 or greater)

### Installation

1. Download from [releases](https://github.com/aleksn41/corona_world_app/releases)

2. Install on Android Device


**OR**

1. Clone the repo
   ```sh
   git clone https://github.com/aleksn41/corona_world_app.git
   ```
2. Build debug APK
   ```sh
   gradlew assembleDebug
   ```


## Usage

Once the app is opened you are conveyed to the starting screen, the world map. In this screen a heat map of the world is shown. All countries are colored from green to red after their respective ratio of how much of the population is infected. Image:
<p align="center">
  <img src="/images/WorldMap.png" alt="World Map">
</p>
In the top right corner of this screen a summary of the whole world is shown. At the bottom above the navigation bar, a small bar with and upwards arror can be clicked. If any country was previously selected it's name and flag are shown in this BottomSheet. Image:
<p align="center">
  <img src="/images/WorldMap_Selected_HalfExpanded.png" alt="World Map - Half Expanded BottomSheet">
</p>
If this BottomSheet is clicked another time, it extends all the way and layers of the full screen. Additional info for the selected country (Infected, Deaths, Recovered, etc.) is shown in this overlay. The user now has also the ability to get a pie chart with the most important info through the "GO TO STATISTICS" button. Image:
<p align="center">
  <img src="/images/WorldMap_Selected_FullyExpanded.png" alt="World Map - Fully Expanded BottomSheet">
</p>

Click [here](https://github.com/aleksn41/corona_world_app/wiki/Overview) for a quick overview of all features and functionalities. 

## Roadmap

See the [open issues](https://github.com/aleksn41/corona_world_app/issues) for a list of proposed features (and known issues).


<!-- LICENSE
## License

Distributed under the  License. See `LICENSE` for more information.


 -->
<!-- CONTACT
## Contact

Your Name - [@twitter_handle](https://twitter.com/twitter_handle) - email

Project Link: [https://github.com/github_username/repo_name](https://github.com/github_username/repo_name)

-->

## APIs
- https://coronavirus-19-api.herokuapp.com (for live data of the world map and statistics)
- https://restcountries.eu/rest/v2 (for population data)
- https://api.covid19api.com (for past statistics)
- https://opendata.arcgis.com/datasets/ef4b445a53c1406892257fe63129a8ea_0.geojson (for live data of germany's states)

## Libraries
- Google Charts https://developers.google.com/chart (for displaying the maps)
- MPAndroid Chart https://github.com/PhilJay/MPAndroidChart (for displaying charts)
- okhttp3 https://square.github.io/okhttp/ (for making API requests)


<!-- MARKDOWN LINKS & IMAGES -->
<!-- https://www.markdownguide.org/basic-syntax/#reference-style-links -->
[contributors-shield]: https://img.shields.io/github/contributors/aleksn41/corona_world_app.svg?style=for-the-badge
[contributors-url]: https://github.com/aleksn41/corona_world_app/graphs/contributors
[forks-shield]: https://img.shields.io/github/forks/aleksn41/corona_world_app.svg?style=for-the-badge
[forks-url]: https://github.com/aleksn41/corona_world_app/network/members
[stars-shield]: https://img.shields.io/github/stars/aleksn41/corona_world_app.svg?style=for-the-badge
[stars-url]: https://github.com/aleksn41/corona_world_app/stargazers
[issues-shield]: https://img.shields.io/github/issues/aleksn41/corona_world_app.svg?style=for-the-badge
[issues-url]: https://github.com/aleksn41/corona_world_app/issues
[license-shield]: https://img.shields.io/github/license/aleksn41/corona_world_app.svg?style=for-the-badge
[license-url]: https://github.com/aleksn41/corona_world_app/blob/master/LICENSE.txt
