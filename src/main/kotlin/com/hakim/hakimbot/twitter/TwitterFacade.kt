package com.hakim.hakimbot.twitter

import twitter4j.Tweet
import twitter4j.TwitterFactory
import twitter4j.conf.ConfigurationBuilder
import twitter4j.v2

class TwitterFacade(twitterData: TwitterData) {
    private val twitterApi = TwitterFactory(
        ConfigurationBuilder()
            .setOAuthConsumerKey(twitterData.consumerKey)
            .setOAuthConsumerSecret(twitterData.consumerSecret)
            .setOAuthAccessToken(twitterData.accessToken)
            .setOAuthAccessTokenSecret(twitterData.accessTokenSecret)
            .build()
    ).instance.v2

    fun readUserTweets(userId: Long): List<Tweet> {
        return twitterApi.searchRecent("from:$userId -is:retweet -is:quote -is:reply", maxResults = 10).tweets
    }
}