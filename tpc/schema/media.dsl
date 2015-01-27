module media {

    enum Size {
        SMALL;
        LARGE;
    }

    enum Player {
        JAVA;
        FLASH;
    }

    value Image {
        String uri;
        String? title;
        int width;       
        int height;
        Size size;
    }

    value Media {
        String uri;
        String? title;
        int width;
        int height;
        String format;
        long duration;
        long size;
        int bitrate;
        List<String> persons;
        Player player;
        String? copyright;
    }

    value MediaContent {
        Media media;
        List<media.Image> images;
    }
}
