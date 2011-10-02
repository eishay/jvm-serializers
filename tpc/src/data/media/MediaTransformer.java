package data.media;

import serializers.Transformer;

public abstract class MediaTransformer<B> extends Transformer<MediaContent,B>
{
    public MediaContent[] sourceArray(int size) { return new MediaContent[size]; }

    // just defined to work around Scala issue
    public B[] resultArray(int size) { throw new UnsupportedOperationException("Please implement for "+getClass().getName()); }
}
