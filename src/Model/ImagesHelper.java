package Model;

import Control.ParentController;
import javafx.scene.image.Image;

import java.util.ArrayList;
import java.util.List;

/**
 * this class handles images for the helper pop up
 * it loads all the images from the resources folder into an array list which can be
 * accessed through the instance of the class
 *
 * @author: Lucy Chen
 */
public class ImagesHelper {

    private Image HOME = new Image(getClass().getResourceAsStream("/helperImages/Main1.png"));
    private Image PRACTICE1 = new Image(getClass().getResourceAsStream("/helperImages/Practice2.png"));
    private Image PRACTICE2 = new Image(getClass().getResourceAsStream("/helperImages/Practice3.png"));
    private Image PRACTICE3 = new Image(getClass().getResourceAsStream("/helperImages/Practice3_9.png"));
    private Image PRACTICE4 = new Image(getClass().getResourceAsStream("/helperImages/Practice4.png"));
    private Image PRACTICE5 = new Image(getClass().getResourceAsStream("/helperImages/Practice5.png"));
    private Image CHALLENGE6 = new Image(getClass().getResourceAsStream("/helperImages/Challenge6.png"));
    private Image CHALLENGE7 = new Image(getClass().getResourceAsStream("/helperImages/Challenge7.png"));
    private Image CHALLENGE8 = new Image(getClass().getResourceAsStream("/helperImages/Challenge8.png"));
    private Image CHALLENGE9 = new Image(getClass().getResourceAsStream("/helperImages/Challenge9.png"));
    private Image CHALLENGE10 = new Image(getClass().getResourceAsStream("/helperImages/Challenge10.png"));
    private Image STATS11 = new Image(getClass().getResourceAsStream("/helperImages/Stats11.png"));
    private Image HEADER12 = new Image(getClass().getResourceAsStream("/helperImages/Header12.png"));
    private Image MIC13 = new Image(getClass().getResourceAsStream("/helperImages/Mic13.png"));

    private List<Image> images;

    private Image _currentImage;
    private int _index;

    private final static ImagesHelper instance = new ImagesHelper();

    public static ImagesHelper getInstance() {
        return instance;
    }

    /**
     * adds all the images to a list
     */
    private ImagesHelper() {
        images = new ArrayList<>();
        images.add(HOME);
        images.add(PRACTICE1);
        images.add(PRACTICE2);
        images.add(PRACTICE3);
        images.add(PRACTICE4);
        images.add(PRACTICE5);
        images.add(CHALLENGE6);
        images.add(CHALLENGE7);
        images.add(CHALLENGE8);
        images.add(CHALLENGE9);
        images.add(CHALLENGE10);
        images.add(STATS11);
        images.add(HEADER12);
        images.add(MIC13);
        _currentImage = HOME;
        _index = 0;

    }

    /**
     * gets the current image
     * @return
     */
    public Image getCurrentImage() {
        try {
            return _currentImage;
        } catch (IndexOutOfBoundsException e) {
            _currentImage = HOME;
        }
        return null;
    }

    /**
     * updates the current image to the next image
     * @return
     */
    public Image nextImage() {
        _index = _index + 1;
        _currentImage = images.get(_index);
        return getCurrentImage();
    }

    /**
     * updates the current image to the previous one
     * @return
     */
    public Image previousImage() {

        _index = _index - 1;
        _currentImage = images.get(_index);
        return getCurrentImage();
    }

    /**
     * checks if current image is last image
     * @return
     */
    public Boolean lastImage() {
        if (_index == images.size() - 1) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * checks in image is first one
     * @return
     */
    public Boolean firstImage() {

        if (_index == 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * this method sets the current page the user is on, to display relevant helper information.
     * @param page
     */
    public void setImage(ParentController.PageType page) {
        if (page == ParentController.PageType.CHALLENGE) {
            _currentImage = CHALLENGE6;
        } else if (page == ParentController.PageType.PRACTICE) {
            _currentImage = PRACTICE1;
        } else if (page == ParentController.PageType.CHALLENGECOMPARE) {
            _currentImage = CHALLENGE9;
        } else if (page == ParentController.PageType.LISTEN) {
            _currentImage = CHALLENGE10;
        } else if (page == ParentController.PageType.STATS) {
            _currentImage = STATS11;
        } else {
            _currentImage = HOME;
        }
    }
}
