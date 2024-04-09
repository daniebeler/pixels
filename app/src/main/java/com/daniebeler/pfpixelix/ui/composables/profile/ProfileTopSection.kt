package com.daniebeler.pfpixelix.ui.composables.profile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.TableRows
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.daniebeler.pfpixelix.R
import com.daniebeler.pfpixelix.domain.model.Account
import com.daniebeler.pfpixelix.ui.composables.hashtagMentionText.HashtagsMentionsTextView
import com.daniebeler.pfpixelix.utils.Navigate
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale

@Composable
fun ProfileTopSection(
    account: Account?, navController: NavController, openUrl: (url: String) -> Unit, changeView: (ViewEnum) -> Unit, view: ViewEnum
) {
    if (account != null) {
        Column(Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(
                    model = account.avatar,
                    contentDescription = "",
                    modifier = Modifier
                        .height(76.dp)
                        .width(76.dp)
                        .clip(CircleShape)
                )

                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = String.format(Locale.GERMANY, "%,d", account.postsCount),
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        Text(text = stringResource(R.string.posts), fontSize = 12.sp)
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable {
                            Navigate.navigate(
                                "followers_screen/" + "followers/" + account.id, navController
                            )
                        }) {
                        Text(
                            text = String.format(Locale.GERMANY, "%,d", account.followersCount),
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        Text(text = stringResource(R.string.followers), fontSize = 12.sp)
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable {
                            Navigate.navigate(
                                "followers_screen/" + "following/" + account.id, navController
                            )
                        }) {
                        Text(
                            text = String.format(Locale.GERMANY, "%,d", account.followingCount),
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        Text(text = stringResource(R.string.following), fontSize = 12.sp)
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            if (account.displayname != null) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = account.displayname, fontWeight = FontWeight.Bold)
                    if (account.locked) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.Outlined.Lock,
                            contentDescription = null,
                            Modifier.size(16.dp)
                        )
                    }
                }
            }

            if (account.note.isNotBlank()) {
                HashtagsMentionsTextView(text = account.note,
                    mentions = null,
                    navController = navController,
                    openUrl = { url -> openUrl(url) })
            }

            account.website?.let {
                Row(Modifier.padding(top = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.Language,
                        contentDescription = "",
                        modifier = Modifier.size(18.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = account.website.toString().substringAfter("https://"),
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.clickable(onClick = { openUrl(account.website.toString()) })
                    )
                }
            }
            Row(
                horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()
            ) {
                if (account.createdAt.isNotBlank()) {
                    val date: LocalDate = LocalDate.parse(account.createdAt.substringBefore("T"))
                    val formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
                    val formatted = date.format(formatter)
                    Text(
                        text = "Joined $formatted",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 10.sp
                    )
                } else {
                    Spacer(modifier = Modifier.weight(1f))
                }
                Row {
                    Box(modifier = Modifier.padding(4.dp).clickable{ changeView(ViewEnum.Grid) }.alpha(if (view == ViewEnum.Timeline) {0.5f} else {1f})) {
                        Icon(
                            imageVector = Icons.Outlined.GridView, contentDescription = "grid view"
                        )
                    }
                    Box(modifier = Modifier.padding(4.dp).clickable{ changeView(ViewEnum.Timeline) }.alpha(if (view == ViewEnum.Grid) {0.5f} else {1f})) {
                        Icon(
                            imageVector = Icons.Outlined.TableRows,
                            contentDescription = "timeline view"
                        )
                    }
                }
            }
        }
    }
}