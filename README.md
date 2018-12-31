# P2P-VideoStreaming
A java application based on peer to peer networking paradigm for video streaming.

The idea underlying this project is the restrictions that are being imposed by **Popular websites and content providers**, which todays modern minds think of as a possible threat towards the open-minded communities like ours.

![We want neutrality](https://i.imgur.com/14ZNNZf.gifv)

## Few Features:

- [x] A user account where each user can be content creator or viewer (or both).
- [x] Content creators can have multiple channels that they can manage.
- [x] Viewers can subscribe to any channel and they must also get notified every time a new video is added to the channel they are subscribed to.
- [x] Viewers can search all the videos available over the network by the video title or tags.
- [x] Viewer can also like, comment and add a video to their watch-later list.
- [x] Content creators can also add tags to video whenever they add video.- [x] A trending-videos page, where viewers can see the list of trending videos
- [x] Two plans for content creators - free and premium.
	* Free plan - content creator have to keep their instance of Application up and running(if their instance goes down, their content will be immediately unavailable).
	* Premium plan - a copy of all the content added by the creator(with premium membership) will be stored at one extra peer (who may be a content creator or a content viewer) and in case of downtime of the original creator’s instance,this cache-peer would be used for streaming purposes.
- [x] Recommendations to users.
