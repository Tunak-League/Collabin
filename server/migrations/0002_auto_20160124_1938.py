# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.db import models, migrations
import cloudinary.models


class Migration(migrations.Migration):

    dependencies = [
        ('server', '0001_initial'),
    ]

    operations = [
        migrations.AlterField(
            model_name='userprofiles',
            name='user_image',
            field=cloudinary.models.CloudinaryField(default=None, max_length=255, null=True, verbose_name=b'image', blank=True),
        ),
    ]
