$(document).on('click', '.button_tab', function (e) {
    var $this = $(this),
        $type = $this.attr('data-type'),
        $direction = $this.attr('data-direction'),
        $ship = $this.parents('.ship'),
        $tabs = $ship.find('.button_tab.select[data-direction=' + $direction + ']'),
        $panels = $ship.find('.panel_' + $direction + '.show'),
        $info = $ship.find('.panel_' + $direction + '[data-type=' + $type + ']');

    for (var i = 0; i < $tabs.length; i++) {
        $tabs.eq(i).removeClass('select');
    }
    $this.addClass('select');

    for (var i = 0; i < $panels.length; i++) {
        $panels.eq(i).removeClass('show').addClass('hide');
    }
    $info.removeClass('hide').addClass('show');
});

$(document).on('click', '.button_module', function(){
    var $this = $(this),
        $index = parseInt($this.attr('data-index')),
        $type = $this.attr('data-type'),
        $pos = parseInt($this.attr('data-position')),
        $prevType = $this.attr('data-prev-type'),
        $prevPos = parseInt($this.attr('data-prev-position')),
        $ship = $this.parents('.ship'),
        $all = $ship.find('.button_module.select'),
        $modules = $ship.find('.button_module.select[data-index=' + $index + ']');

    if ($this.hasClass('select')) {
        return false;
    }

    for (var i = 0; i < $all.length; i++) {
        var $cur = $all.eq(i),
            $curType = $cur.attr('data-type'),
            $curPos = parseInt($cur.attr('data-position')),
            $curPrevType = $cur.attr('data-prev-type'),
            $curPrevPos = parseInt($cur.attr('data-prev-position'));

        if ((($curPrevType === $type && $curPrevPos > $pos) || ($prevType === $curType && $prevPos > $curPos)) && $curType !== $type) {
            return false;
        }
    }

    for (var i = 0; i < $modules.length; i++) {
        $modules.eq(i).removeClass('select');
        var $subImage = $modules.eq(i).find('img');
        $subImage.attr('src', $subImage.attr('src').replace('_installed.png', '.png'));
    }
    $this.addClass('select');
    var $image = $this.find('img');
    $image.attr('src', $image.attr('src').replace('.png', '_installed.png'));

    delayCall($ship);
});

$(document).on('click', '.button_upgrade', function (e) {
    e.stopPropagation();
    var $this = $(this),
        $index = parseInt($this.attr('data-index')),
        $ship = $this.parents('.ship'),
        $upgrades = $ship.find('.button_upgrade[data-index=' + $index + ']');

    if (!$this.hasClass('select')) {
        for (var i = 0; i < $upgrades.length; i++) {
            $upgrades.eq(i).removeClass('select');
            $upgrades.eq(i).addClass('hide');
        }
        $this.addClass('select');
        $this.removeClass('hide');
        delayCall($ship);
    } else {
        $upgrades.removeClass('hide');
    }
});

$(document).on('click', '.button_consumable', function (e) {
    e.stopPropagation();
    var $this = $(this),
        $index = parseInt($this.attr('data-index')),
        $ship = $this.parents('.ship'),
        $consumables = $ship.find('.button_consumable[data-index=' + $index + ']');

    if (!$this.hasClass('select')) {
        for (var i = 0; i < $consumables.length; i++) {
            $consumables.eq(i).removeClass('select');
            // $consumables.eq(i).addClass('hide');
        }
        $this.addClass('select');
        // $this.removeClass('hide');
    // } else {
        // $consumables.removeClass('hide');
    }
});

$(document).on('click', function () {
    var $upgrades = $('.button_upgrade'),
        $consumables = $('.button_consumable');

    for (var i = 0; i < $upgrades.length; i++) {
        if (!$upgrades.eq(i).hasClass('select')) {
            $upgrades.eq(i).addClass('hide');
        }
    }

    // for (var i = 0; i < $consumables.length; i++) {
    //     if (!$consumables.eq(i).hasClass('select')) {
    //         $consumables.eq(i).addClass('hide');
    //     }
    // }
});

var $maxSpts = 21;
$(document).on('click', '.button_skill', function () {
    var $this = $(this),
        $index = parseInt($this.attr('data-index')),
        $pos = parseInt($this.attr('data-position')),
        $ship = $this.parents('.ship'),
        $pts = $ship.find('.panel_left[data-type=crewSkills]').find('.points'),
        $check = $ship.find('.limit_skill'),
        $sCommander = $ship.find('[name=commander]').val(),
        $curCommander = $ship.find('[data-commander-index=' + $sCommander + ']'),
        $skills = $curCommander.find('.button_skill.select'),
        $totalSpts = 0;

    var canUse = true;
    var hasIndex = [false, false, false, false];

    if (!$this.hasClass('select')) {
        hasIndex[$index] = true;
        $totalSpts = $totalSpts + $index + 1;
    }

    for (var i = 0; i < $skills.length; i++) {
        var $sIndex = parseInt($skills.eq(i).attr('data-index'));
        var $sPos = parseInt($skills.eq(i).attr('data-position'));

        if ($sIndex !== $index || $sPos !== $pos) {
            $totalSpts = $totalSpts + $sIndex + 1;
            hasIndex[$sIndex] = true;
        }
    }

    if ($check.is(':checked')) {
        for (var i = 3; i > 0; i--) {
            if (hasIndex[i] && !hasIndex[i - 1]) {
                canUse = false;
                break;
            }
        }
    }

    if (!canUse || $totalSpts > $maxSpts) {
        return false;
    }

    if ($this.hasClass('select')) {
        $ship.find('.button_skill[data-index=' + $index + '][data-position=' + $pos +']').removeClass('select');
    } else {
        $ship.find('.button_skill[data-index=' + $index + '][data-position=' + $pos +']').addClass('select');
    }

    var adrenalRush = $curCommander.find('.button_skill[data-ar=true]');
    var adrenalSlider = $ship.find('.adrenalineRush');
    if (adrenalRush.hasClass('select')) {
        adrenalSlider.css('display', 'inline-flex')
    } else {
        adrenalSlider.css('display', 'none');
        $ship.find('.arSlider').val(100);
        $ship.find('.arSliderValue').text('100');
    }

    $pts.text($totalSpts);

    delayCall($ship);
});

$(document).on('click', '.button_flag', function () {
    var $this = $(this),
        $index = parseInt($this.attr('data-index')),
        $ship = $this.parents('.ship'),
        $pts = $ship.find('.panel_left[data-type=flags]').find('.points'),
        $flags = $ship.find('.button_flag.select'),
        $totalFpts = 0,
        $maxPts = parseInt($ship.find('.panel_left[data-type=flags]').find('.maxPoints').attr('data-max-points'));

    for (var i = 0; i < $flags.length && $maxPts >= $totalFpts; i++) {
        $totalFpts += 1;
    }

    if ($this.hasClass('select')) {
        $ship.find('.button_flag[data-index=' + $index + ']').removeClass('select');
        $totalFpts -= 1;
    } else {
        if ($totalFpts >= $maxPts) {
            return false;
        }

        $ship.find('.button_flag[data-index=' + $index + ']').addClass('select');
        $totalFpts += 1;
    }

    $pts.text($totalFpts);

    delayCall($ship);
});

$(document).on('click', '.switch', function (e) {
    var $this = $(this),
        $type = $this.attr('data-type'),
        $toggle = $this.parent().find('.' + $type);

    for (var i = 0; i < $toggle.length; i++) {
        if ($toggle.eq(i).hasClass('toggle')) {
            if ($toggle.eq(i).hasClass('hide')) {
                $toggle.eq(i).removeClass('hide');
            } else {
                $toggle.eq(i).addClass('hide');
            }
        }
    }
});

var waitTime = 1500;
var timer = [];
function delayCall($ship)
{
    var index = $ship.attr('data-ship-index') + '_' + $ship.attr('data-ship-position');
    window.clearTimeout(timer[index]);
    timer[index] = window.setTimeout(function() {
        callPage($ship);
    }, waitTime);
}

function makeUrl($ship)
{
    var sModules = $ship.find('.button_module.select');
    var sUpgrades = $ship.find('.button_upgrade.select');
    var $sCommander = $ship.find('[name=commander]').val(),
        $curCommander = $ship.find('[data-commander-index=' + $sCommander + ']'),
        $sSkills = $curCommander.find('.button_skill.select');
    var $sFlags = $ship.find('.button_flag.select');
    var sConsumables = $ship.find('.button_consumable.select');

    var modules = '';
    var mArrays = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0];
    for (var i = 0; i < sModules.length; i++) {
        mArrays[sModules.eq(i).attr('data-index')] = parseInt(sModules.eq(i).attr('data-position')) + parseInt(sModules.eq(i).attr('data-temp-position'));
    }
    for (var x in mArrays) {
        modules += (mArrays[x] === 0 ? '' : mArrays[x].toString());
    }

    var upgrades = '';
    var uArrays = [0, 0, 0, 0, 0, 0];
    for (var i = 0; i < sUpgrades.length; i++) {
        uArrays[sUpgrades.eq(i).attr('data-index')] = parseInt(sUpgrades.eq(i).attr('data-position'));
    }
    for (var x in uArrays) {
        upgrades += (uArrays[x] === 0 ? '0' : uArrays[x].toString());
    }

    var commander = $("[name=commander]").val();

    var useAr = false;
    var skills = 0;
    for (var i = 0; i < $sSkills.length; i++) {
        var arUse = $sSkills.eq(i).attr('data-ar')
        var pos = parseInt($sSkills.eq(i).attr('data-index')) * 6 + parseInt($sSkills.eq(i).attr('data-position'));
        skills += Math.pow(2, pos);
        if (arUse === 'true') {
            useAr = true;
        }
    }

    var flags = 0;
    for (var i = 0; i < $sFlags.length; i++) {
        var pos = parseInt($sFlags.eq(i).attr('data-index')) - 1;
        flags += Math.pow(2, pos);
    }

    var ar = useAr && $ship.find('.arSlider').val() !== undefined ? $ship.find('.arSlider').val() : 0;

    var consumables = '';
    var cArrays = [0, 0, 0, 0, 0];
    for (var i = 0; i < sConsumables.length; i++) {
        cArrays[sConsumables.eq(i).attr('data-index')] = parseInt(sConsumables.eq(i).attr('data-position'));
    }
    for (var x in cArrays) {
        consumables += (cArrays[x] === 0 ? '' : cArrays[x].toString());
    }

    var $shipIndex = $ship.attr('data-ship-index');

    var url =
        '/ship?index=' + $shipIndex
        + (modules !== '' ? '&modules=' + modules : '')
        + (upgrades.replace('0') !== '' ? '&upgrades=' + upgrades : '')
        + '&commander=' + commander.toUpperCase()
        + (skills > 0 ? '&skills=' + skills.toString() : '')
        + (ar > 0 ? '&ar=' + ar : '')
        + (flags > 0 ? '&flags=' + flags.toString() : '')
        + (consumables !== '' ? '&consumables=' + consumables : '')
        + '&pos=' + $ship.attr('data-ship-position');
        // + '&lang=' + lang;

    return url;
}

function callPage($ship)
{
    var $toggle = $ship.find('.toggle');
    var $toggleDecide = {};

    for (var i = 0; i < $toggle.length; i++) {
        if ($toggle.eq(i).hasClass('hide')) {
            $toggleDecide[$toggle.eq(i).attr('class').replace('toggle', '').replace('hide', '').trim()] = 'hide';
        } else {
            $toggleDecide[$toggle.eq(i).attr('class').replace('toggle', '').trim()] = '';
        }
    }
    var $checked = $ship.find('.limit_skill').is(':checked');

    var $shipIndex = $ship.attr('data-ship-index');
    var $shipPosition = $ship.attr('data-ship-position');
    var $shipDom = $('.ship[data-ship-index=' + $shipIndex + '][data-ship-position=' + $shipPosition + ']');
    var url = makeUrl($ship);

    $.ajax({
        url: url,
        type: 'post',
        success: function (data) {
            if (data.status === undefined) {
                $shipDom.find('.info_box.replace').remove();
                $shipDom.append(data);
                // $('.ship').remove();
                // $('.main').prepend(data);

                for (var x in $toggleDecide) {
                    var temp = '.toggle.' + x.toString();
                    if ($toggleDecide[x] === 'hide') {
                        $('[data-ship-index=' + $shipIndex + '][data-ship-position=' + $shipPosition + ']').find(temp).addClass('hide')
                    } else {
                        $('[data-ship-index=' + $shipIndex + '][data-ship-position=' + $shipPosition + ']').find(temp).removeClass('hide')
                    }
                }
                // $('[data-ship-index=' + $shipIndex + ']').find('.limit_skill').prop('checked', $checked);

                if (!compare) {
                    history.replaceState({
                        id: $shipIndex
                    }, '', url);
                }
            } else {
                console.log(data);
            }
        },
        error: function (data) {
            console.log(data)
        }
    })
}

$(document).on('input mouseup', '.arSlider', function (e) {
    var $this = $(this),
        $ship = $this.parents('.ship'),
        $parent = $this.parents('label'),
        $sliderVal = $parent.find('.arSliderValue');

    $sliderVal.text($this.val().toString());

    if (e.type === 'mouseup') {
        delayCall($ship);
    }
});

$(document).on('change', '[name=commander]', function () {
    var $this = $(this),
        $ship = $this.parents('.ship'),
        $commanders = $ship.find('.commander_body'),
        $sCommander = $ship.find('[name=commander]').val(),
        $curCommander = $ship.find('[data-commander-index=' + $sCommander + ']'),
        $skills = $curCommander.find('.button_skill.select');

    $commanders.addClass('disable');
    $curCommander.removeClass('disable');

    if ($skills.length) {
        delayCall($ship);
    }
});

$(document).on('click', '.shipsList_expand .shipsList_top', function (e) {
    // e.stopPropagation();
    var $this = $(this),
        $nations = $('.nation');

    if ($this.hasClass('expand')) {
        $this.removeClass('expand');
        $this.addClass('contract');
        $nations.removeClass('hide');
    } else {
        $this.removeClass('contract');
        $this.addClass('expand');
        $nations.addClass('hide');
    }
});

$(document).on('click', '#adToggle', function () {
    var $this = $(this),
        $status = $this.attr('data-status') !== '1';

    $.ajax({
        url: '/adToggle?toggle=' + $status.toString(),
        type: 'post',
        success: function (data) {
            if (data.status === '200') {
                location.reload()
            } else {
                console.log(data);
            }
        },
        error: function (data) {
            console.log(data)
        }
    })
})

$(document).on('click', '#shipSelect', function () {
    var url = $('#shipUrl').val();

    if (url === undefined) {
        return false;
    } else {
        var split = url.split('/ship');
        if (split.length !== 2) {
            return false;
        }
        url = '/compare' + split[1];
        curIndex++;
        url = url + '&pos=' + curIndex;
        url = url.replace('&pos=0', '');
    }

    $.ajax({
        url: url,
        type: 'post',
        success: function (data) {
            if (data.status === undefined) {
                $('.comparison').append(data);
            }
        }
    });
})

$(document).on('click', '#removeShip', function () {
    var $this = $(this),
        $ship = $this.parents('.ship');
    $ship.remove();
})