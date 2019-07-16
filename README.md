# ChesterCinema
Based off of the concpet of [MovieBot](https://github.com/RubbaBoy/MovieBot). Plays sequential gifs with audio for a "seamless" video experience. This is an experiment in loading more frames and caching them. Frames are loade "buffered" asynchrously. Caching is done by saving the link to a file for next use and put less strain on Discord hopefully. (No need to reupload links/gif every video play) This also can grab any video from YouTube and other supported sites that [YouTube-DL](https://ytdl-org.github.io/youtube-dl/index.html) supports. This bot is more of a proof of concept and experiment. The bot may not be reliable and there will be bugs.
# Requirements
This bot runs on Java 12 platform. During runtime, the bot requires:

 - ffmpeg
 - ffprobe
 - YouTubeDL (for downloading videos from url)

# Setup

 1. Build the jar using Maven
 2. Run the jar.
    The token and other options can be set in config.json generated after first run.

You can create a videos folder and put .mp4 there. This will also be the place where videos are downloaded to.

# Play Movies/Videos

 - !movie - Plays a video file locally. Subcommands are: list, stop
 - !onlinemovie - Downloads and plays a video from YouTube. Provide an url or search query.



