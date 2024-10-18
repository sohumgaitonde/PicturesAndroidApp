In regards to how I did this project, I will break it down into two sections: first, API communication and second, UI and styling. 

Regarding API communication, in MainComposeActivity, in the OnCreate method, I first set up Retrofit for making API requests. In this method, I created the retrofit instance for configuration, specified the base URL for the API, and added a converter factory for deserializing the JSON responses from the API to be used. pictureService is then declared as an implementation of the given PictureService interface which is used in the composable functions in PictureView.kt which I will outline later. Note that although I was able to meet all milestones for this project,, the actual images retrieved from the API call are not solely cats and birds. The given picsum.photos/v2 includes other types of images, and filtering to get just these two image types was not available. Also, in this file, I call CatPicturesApp(), passing in pictureService as an argument. I will outline the purpose of this composable with more detail below.

With the API configuration done, the next focus was creating the actual frontend, which was done in the PictureView.kt file. The first composable function in this file, CatPicturesApp, establishes the basic UI structure of the app. Here, we have two state variables declared, currentScreen which is a String that tracks whether either Home or Favorites screen is selected and favoritedPictures which is a set that holds the pictures that have been favorited. Since this composable is the entry composable, that is called from MainComposeActivity, I set up the nav bar UI and logic here via a Scaffold component. I used a BottomNavigation bar with two BottomNavigationItems, for Home and Favorites respectively, and integrated the clicking and switching screens logic via a ‘when statement’. Specifically, when the state variable currentScreen is Home, we call the composable FlexiblePictureScreen (with isFavoritesScreen argument set to false) and when currentScreen is Favorites, we call the composable FlexiblePictureScreen (with isFavoritesScreen argument set to true).  A key decision here was using a common composable regardless of screen to avoid redundancy, which I will outline more below.

With this basic UI and navigating logic set up, FlexiblePictureScreen needed to be implemented. The goal of this composable was to create a flexible composable for displaying the grid of images, that supports the basic uniform grid of pictures, but also the enlarged version of the picture when it is selected. We have two important state variables here, pictures, which holds a list of all pictures we want to display and currentPicture which holds the single picture that is currently selected. The next part of the implementation was based on the distinction of whether the user was in Home or Favorites. If the former, a call to the API would be made to fetch the pictures and render them on the screen. If the latter, the pictures that would be displayed would be those in the favoritedPictures set, which is an argument. Next, there are two possible UI considerations on this screen. First, if the user has an image selected. In this case, we would want the image to be at the top and enlarged. Briefly, I used several Compose components like Column and Box, and within these, Image and Icon, to display the enlarged picture at the top as well as the Favorite button. Elaborating on the Icon, when the heart was clicked, if the respective picture was already in favoritePictures, we would remove the picture from the set and, if we were on the Favorites screen, we immediately update the pictures shown since it is no longer a favorite. If this picture was not already favorited, we would add it to the favoritedPictures set. The bottom-most element of the Column was PictureGrid(), which is the last composable, which depicts all the pictures in a grid uniformly. 

Here, I leveraged Compose’s LazyVerticalGrid component, and passed a list of the pictures which were to be the actual content of the grid. Here, there was also clickable logic which interacted with the PictureScreen composable regarding rendering the enlarged image composable if a picture was selected. Only the PictureGrid portion would be displayed on the screen if an image was not selected, but if an image is selected, this basic grid would appear at the bottom.

The key design consideration here was using a common FlexiblePictureScreen composable for both screens rather than building a separate, redundant one for the two distinct screens. I noticed the two screens both share grids and the potential to have an enlarged image at top, so instead of building these as distinct composables, I used a single FlexiblePictureScreen, where the the distinction between the screens rested in actual pictures state variable value decided in the LaunchedEffect function.

In terms of development order of UI elements, I first created the basic PictureGrid composable to display all pictures in a grid. I then implemented the PictureScreen composable, in order to manage an enlarged view of an image as well as the ‘favoriting’ and ‘unfavoriting’ of an image, which initially just involved changing the values in the set. Next, I implemented CatPicturesApp, and notably the nav bar which powered the UI and logic for navigating between the two screens. This portion of development involved making sure that the favorited pictures were synced between both screens, which was ensured via the common state variable.
