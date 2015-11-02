module shared {
    enum Size {
        SMALL;
        LARGE;
        //external name Java 'data.media.Image.Size';
    }
    enum Player {
        JAVA;
        FLASH;
        //external name Java 'data.media.Media.Player';
    }
}
module full {
    value ImageFull {
        String uri;
        String? title;
        int width;
        int height;
        shared.Size size;
        //external name Java 'data.media.Image';
    }
    value MediaFull {
        String uri;
        String? title;
        int width;
        int height;
        String format;
        long duration;
        long size;
        int bitrate;
        List<String> persons;
        shared.Player player;
        String? copyright;
        //external name Java 'data.media.Media';
    }
    value MediaContentFull {
        MediaFull media;
        List<ImageFull> images;
        //external name Java 'data.media.MediaContent';
    }
}
module minified {
    value ImageMinified {
        String uri { serialization name u; }
        String? title { serialization name t; }
        int width { serialization name w; }
        int height { serialization name h; }
        shared.Size size { serialization name s; }
        //external name Java 'data.media.Image';
    }
    value MediaMinified {
        String uri { serialization name u; }
        String? title { serialization name t; }
        int width { serialization name w; }
        int height { serialization name h; }
        String format { serialization name f; }
        long duration { serialization name d; }
        long size { serialization name s; }
        int bitrate { serialization name b; }
        List<String> persons { serialization name p; }
        shared.Player player { serialization name l; }
        String? copyright { serialization name c; }
        //external name Java 'data.media.Media';
    }
    value MediaContentMinified {
        MediaMinified media { serialization name m; }
        List<ImageMinified> images { serialization name i; }
        //external name Java 'data.media.MediaContent';
    }
}

