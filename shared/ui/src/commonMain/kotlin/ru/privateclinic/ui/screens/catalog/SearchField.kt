package ru.privateclinic.ui.screens.catalog

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.privateclinic.ui.components.icon.ClinicIcon
import ru.privateclinic.ui.components.icon.Symbol
import ru.privateclinic.ui.theme.ClinicTheme
import ru.privateclinic.ui.theme.LocalAccent
import ru.privateclinic.ui.theme.ThemeChoice

@Composable
internal fun SearchField(value: String, onSearch: (String) -> Unit) {
    val palette = LocalAccent.current
    val focus = remember { MutableInteractionSource() }
    val focused by focus.collectIsFocusedAsState()
    val shape = RoundedCornerShape(13.dp)
    val label = "Название или адрес"
    val input = LocalTextStyle.current.copy(color = palette.searchInk, fontSize = 16.sp, lineHeight = 22.sp)
    BasicTextField(value, onSearch, modifier = Modifier.fillMaxWidth().semantics { contentDescription = label },
        textStyle = input, singleLine = true, interactionSource = focus, cursorBrush = SolidColor(palette.searchInk),
        decorationBox = { field ->
            Row(Modifier.fillMaxWidth().heightIn(min = 44.dp).background(palette.search, shape)
                .then(if (focused) Modifier.border(2.dp, MaterialTheme.colorScheme.primary, shape) else Modifier)
                .padding(horizontal = 12.dp), verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Symbol(ClinicIcon.Search, palette.searchInk)
                Box(Modifier.weight(1f)) {
                    if (value.isEmpty()) Text(label, style = input, color = palette.searchInk.copy(alpha = .87f))
                    field()
                }
            }
        })
}

@Preview(showBackground = true, widthDp = 350, heightDp = 44)
@Composable
private fun SearchFieldPreview() {
    ClinicTheme(ThemeChoice.Light) {
        SearchField(value = "", onSearch = {})
    }
}
