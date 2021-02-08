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
### Starting Screen / World Map
Once the app is opened you are conveyed to the starting screen, the world map. In this screen a heat map of the world is shown. All countries are colored from green to red after their respective ratio of how much population is infected. Image:
<p align="center">
  <img src="/images/WorldMap.png" alt="World Map">
</p>
In the top right corner of this screen a summary of the whole world is shown. At the bottom, above the navigation bar a small bar with and upwards arror can be clicked. If any country was previously selected (by clicking) it's name and flag are shown in this BottomSheet. Image:
<p align="center">
  <img src="/images/WorldMap_Selected_HalfExpanded.png" alt="World Map - Half Expanded BottomSheet">
</p>
If this BottomSheet is clicked another time, it extends all the way and layers of the full screen. Additional info for the selected country (Infected, Deaths, Recovered, etc.) is shown in this overlay. The user now has also the ability to get a pie chart with the most important info through the "GO TO STATISTICS" button. Image:
<p align="center">
  <img src="/images/WorldMap_Selected_FullyExpanded.png" alt="World Map - Fully Expanded BottomSheet">
</p>

For more info on the World Map click [here](https://github.com/aleksn41/corona_world_app/wiki/3\)-World-Map).

### Map of Germany
In this screen a Map of Germany and all it's states is shown. The coloring and functionalities are the same as in the World Map. In the top right corner is a summary of germany shown instead of a world overview. Image:
<p align="center">
  <img src="/images/Germany_Selected_HalfExpanded.png" alt="Map of Germany - Half Expanded BottomSheet">
</p>
This time however it is not possible to get a quick overview by creating a pie chart within the BottomSheet. There are also less infos available. (As it is this is only a beta feature, more infos and/or creating a quick pie chart may become available in the future) Image:
<p align="center">
  <img src="/images/Germany_Selected_FullyExpanded.png" alt="Map of Germany - Fully Expanded BottomSheet">
</p>

For more info on the Map of Germany click [here](https://github.com/aleksn41/corona_world_app/wiki/4\)-Germany-Map-(beta)).

### Settings & Info
The global settings and info dropdown can be reached from all fragments. It opens on click at the three vertical dots in the top right corner of the screen. Image (this is from the World Map but the position does not vary):
<p align="center">
  <img src="/images/Settings_Info_Tab.png" alt="Settings & Info Tab">
</p>
Through clicking the user either enters the settings or info screen. In the settings screen the user can en/disable cache (WIP). In the Info screen a short description of the app and it's developers is given.

### Statistic Chooser
In this screen the user can choose which countries, which data, in which time frame and which statistic he wants to be displayed. Image:
<p align="center">
  <img src="/images/StatisticChooser.png" alt="Statistics Chooser">
</p>

When the user enters his parameters he can click on dropdown entries that match his letters. Image:
<p align="center">
  <img src="/images/StatisticChooser_Selection_Dropdown.png" alt="Statistics Chooser Selection Dropdown">
</p>

All selected Parameters will be displayed under their respective input boxes. In case of the countries they are also marked with their according flags. Image:
<p align="center">
  <img src="/images/StatisticChooser_Selection.png" alt="Statistics Chooser Selection">
</p>

When a limit for a certain parameter is reached a hint is given with a red flag. The selection can be finished in clicking on the "CREATE STATISTIC" button in the right bottom corner of the screen. Image:
<p align="center">
  <img src="/images/StatisticChooser_Create.png" alt="Statistics Chooser Create Statistic">
</p>

For more info on the Statistic Chooser click [here](https://github.com/aleksn41/corona_world_app/wiki/5\)-Statistic-Chooser).

### Statistic 
If either the "GO TO STATISTICS" button in the [World Map](https://github.com/aleksn41/corona_world_app/tree/readme#starting-screen--world-map) is clicked or the user created his own statistic through the [Statistic Chooser](https://github.com/aleksn41/corona_world_app/tree/readme#statistic-chooser) a new statistic is created, saved in [History](https://github.com/aleksn41/corona_world_app/tree/readme#history) and then displayed. Example Images:
#### Pie Chart:
<p align="center">
  <img src="/images/Statistic_PieChart.png" alt="Pie Chart">
</p>

#### Line Chart:
<p align="center">
  <img src="/images/Statistic_LineChart.png" alt="Line Chart">
</p>

#### Bar Chart:
<p align="center">
  <img src="/images/Statistic_BarChart.png" alt="Bar Chart">
</p>

For more info on Statistic click [here](https://github.com/aleksn41/corona_world_app/wiki/6\)-Statistic).

### History
(WIP)

### Favourites
(WIP)

Click [here](https://github.com/aleksn41/corona_world_app/wiki/2\)-Overview) for a quick overview of all features and functionalities. 

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

## Known Problems
### Help! I'm getting: 
     
     Error Code 14:
     (API_CURRENTLY_NOT_AVAILABLE)
     Data service is currently unavailable.
     Please try again later.

**Resolution:** See [this](https://github.com/aleksn41/corona_world_app/wiki/Note-on-APIs) for help.
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
